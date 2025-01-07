package com.sap.engine.cache.util.memory;

import java.lang.reflect.*;
import java.util.*;

/**
 * Class providing sizeOf functionality
 *
 * @author Hristo Spaschev Iliev
 * @version 7.2
 * @see <a href="http://www.javaworld.com/columns/jw-qna-index.shtml">Java World article from Vlad Roubtsov</a>
 */
public class SizeOf {
  //{{ determine the bits of the OS
  private static boolean bitsFlag = false;

  static {
    bitsFlag = System.getProperty("sun.arch.data.model", "32").equals("64");
  }
  //}}

  // the following constants are physical sizes (in bytes) and are JVM-dependent
  public static final int OBJECT_SHELL_SIZE = 8 * (bitsFlag ? 2 : 1); // java.lang.Object shell size in bytes
  public static final int OBJREF_SIZE = 4 * (bitsFlag ? 2 : 1);
  public static final int BOOLEAN_FIELD_SIZE = 1;

  /**
   * Returns the size of an object
   *
   * @param obj Object that will be processed
   * @return the size of the object in bytes
   */
  public static int sizeOf(Object obj) {
    return estimate(obj, -1, true);
  }

  /**
   * Returns the size of an object
   *
   * @param obj             Object that will be processed
   * @param maxDepth        Depth of the recursion. If maxDepth is <= 0 then no check is made and the recursion is
   *                        limited only from the object references chain
   * @param ignoreFlyweight Ignore primitive types that are often pooled
   * @return the size of the object in bytes
   */
  public static int estimate(Object obj, int maxDepth, boolean ignoreFlyweight) {

    /* Uses depth-first traversal. The exact graph traversal algorithm does not matter for computing the total size
       and this method could be easily adjusted to do breadth-first instead (addLast() instead of addFirst()).
       dfs/bfs require max queue length to be the length of the longest graph path/width of traversal front
       correspondingly, so it is expected dfs to use fewer resources than bfs for most Java objects */

    if (obj == null) {
      return 0;
    }

    // check for objects with pools or primitive type
    if (ignoreFlyweight && isSharedFlyweight(obj)) {
      return 0;
    }

    final LinkedList<Object> queue = new LinkedList<Object>();
    final IdentityHashMap<Object, Integer> visited = new IdentityHashMap<Object, Integer>();

    visited.put(obj, 0);
    queue.add(obj);

    int result = 0;
    int depth;

    while (!queue.isEmpty()) {
      obj = queue.removeFirst();
      // check if we need to scan deeper
      depth = visited.get(obj);
      if (depth == maxDepth) {
        continue;
      }
      // check for objects with pools or primitive type
      if (ignoreFlyweight && isSharedFlyweight(obj)) {
        continue;
      }
      final Class objClass = obj.getClass();

      if (objClass.isArray()) {
        final int arrayLength = Array.getLength(obj);
        final Class componentType = objClass.getComponentType();

        result += sizeofArrayShell(arrayLength, componentType);

        if (!componentType.isPrimitive()) {
          // traverse each array slot:
          for (int i = 0; i < arrayLength; ++i) {
            final Object ref = Array.get(obj, i);

            if ((ref != null) && !visited.containsKey(ref)) {
              visited.put(ref, depth + 1);
              queue.addFirst(ref);
            }
          }
        }
      } else { // the object is of a non-array type
        final ClassMetadata metadata = getClassMetadata(objClass, ignoreFlyweight);
        if (metadata != null) {
          final Field[] fields = metadata.getAllFields();

          result += metadata.shellSize;

          // traverse all non-null ref fields
          for (int f = 0, fLimit = fields.length; f < fLimit; ++f) {
            final Field field = fields[f];

            Object ref;
            try {
              ref = field.get(obj);
              if (metadata.fieldsToReset.contains(field)) {
                field.setAccessible(false);
              }
            } catch (IllegalArgumentException iarge) {
              //$JL-EXC$
              ref = null;
              System.err.println("Cannot get field [" + field.getName() + "] of class [" + field.getDeclaringClass().getName() + "]");
              iarge.printStackTrace(System.err);
            } catch (IllegalAccessException iacce) {
              //$JL-EXC$
              ref = null;
              System.err.println("Cannot get field [" + field.getName() + "] of class [" + field.getDeclaringClass().getName() + "]");
              iacce.printStackTrace(System.err);
            }

            if ((ref != null) && !visited.containsKey(ref)) {
              visited.put(ref, depth + 1);
              queue.addFirst(ref);
            }
          }
        }
      }
    }

    return result;
  }


  /*
  * A helper method for manipulating a class metadata cache
  *
  * @param cls Class that has to be processes
  * @param filter          SizeOfFilter instance that provides custom rules for traversal
  * @param ignoreFlyweight Ignore primitive types that are often pooled
  * @return ClassMetadata instance with information about the class internals
  */
  private static ClassMetadata getClassMetadata(final Class cls, boolean ignoreFlyweight) {
    if (cls == null ||
        (ignoreFlyweight && isSharedFlyweight(cls))) {
      return null;
    }

    ClassMetadata metadata = new ClassMetadata();

    Field[] declaredFields = null;
    try {
      declaredFields = cls.getDeclaredFields();
    } catch (SecurityException se) {
      //$JL-EXC$
      System.err.println("Cannot access declared fields of class [" + cls.getName() + "]");
      se.printStackTrace(System.err);
    }

    if (declaredFields != null) {
      for (final Field field : declaredFields) {
        if ((Modifier.STATIC & field.getModifiers()) != 0) {
          continue;
        }

        final Class fieldType = field.getType();
        if (fieldType.isPrimitive()) {
          metadata.shellSize += sizeofPrimitiveType(fieldType); // memory alignment ignored
        } else {
          // prepare for graph traversal later:
          if (!field.isAccessible()) {
            try {
              field.setAccessible(true);
              metadata.addField(field, true);
            } catch (SecurityException se) {
              //$JL-EXC$
              System.err.println("Cannot make field [" + field + "] accessible");
              se.printStackTrace(System.err);
            }
          }

          metadata.shellSize += OBJREF_SIZE; // memory alignment ignored
          metadata.addField(field, false);
        }
      }
    }

    // recurse into superclass:
    final ClassMetadata superMetadata = getClassMetadata(cls.getSuperclass(), ignoreFlyweight);
    if (superMetadata != null) {
      metadata.shellSize += superMetadata.shellSize - OBJECT_SHELL_SIZE;
      metadata.refFields.addAll(superMetadata.refFields);
      metadata.fieldsToReset.addAll(superMetadata.fieldsToReset);
    }

    return metadata;
  }

  /**
   * Returns the JVM-specific size of a primitive type.
   *
   * @param type Type of the field
   * @return size of the field
   */
  private static int sizeofPrimitiveType(final Class type) {
    if (type == int.class) {
      return Integer.SIZE;
    } else if (type == long.class) {
      return Long.SIZE;
    } else if (type == short.class) {
      return Short.SIZE;
    } else if (type == byte.class) {
      return Byte.SIZE;
    } else if (type == boolean.class) {
      return BOOLEAN_FIELD_SIZE;
    } else if (type == char.class) {
      return Character.SIZE;
    } else if (type == double.class) {
      return Double.SIZE;
    } else if (type == float.class) {
      return Float.SIZE;
    } else {
      throw new IllegalArgumentException("Type [" + type + "] is not a primitive type");
    }
  }

  /*
  * Computes the "shallow" size of an array instance.
  *
  * @param length Length of the array
  * @param componentType Type of the components
  */
  private static int sizeofArrayShell(final int length, final Class componentType) {
    // Ignores memory alignment issues by design
    final int slotSize = componentType.isPrimitive()
                         ? sizeofPrimitiveType(componentType)
                         : OBJREF_SIZE;

    return OBJECT_SHELL_SIZE + Integer.SIZE + OBJREF_SIZE + length * slotSize;
  }

  /**
   * Returns true if this is a well-known shared flyweight. For example, interned Strings, Booleans and Number objects.
   * <p/>
   * thanks to Dr. Heinz Kabutz see http://www.javaspecialists.co.za/archive/Issue142.html
   *
   * @param obj Object to check
   * @return TRUE if the object is shared flyweight
   */
  private static boolean isSharedFlyweight(Object obj) {
    // optimization - all of our flyweights are Comparable
    if (obj instanceof Comparable) {
      if (obj instanceof Enum) {
        return true;
// !!! Do not uncomment: the code will cause all strings to go in the String internal pool !!!
//      } else if (obj instanceof String) {
//        return (obj == ((String) obj).intern());
// !!! Do not uncomment: the code will cause all strings to go in the String internal pool !!!
      } else if (obj instanceof Boolean) {
        return (obj == Boolean.TRUE || obj == Boolean.FALSE);
      } else if (obj instanceof Integer) {
        return (obj == Integer.valueOf((Integer) obj));
      } else if (obj instanceof Short) {
        return (obj == Short.valueOf((Short) obj));
      } else if (obj instanceof Byte) {
        return (obj == Byte.valueOf((Byte) obj));
      } else if (obj instanceof Long) {
        return (obj == Long.valueOf((Long) obj));
      } else if (obj instanceof Character) {
        return (obj == Character.valueOf((Character) obj));
      }
    }
    return false;
  }

  /**
   * Internal class used to cache class metadata information.
   */
  private static final class ClassMetadata {
    int shellSize = OBJECT_SHELL_SIZE; // java.lang.Object shell
    HashSet<Field> refFields = new HashSet<Field>(); // cached non-static fields (all are accessible)
    HashSet<Field> fieldsToReset = new HashSet<Field>(); // fields that are set accessible

    public Field[] getAllFields() {
      int refSize = refFields.size();
      int resetSize = fieldsToReset.size();
      Field[] fields = new Field[refSize + resetSize];

      if (refSize != 0) {
        Field[] refFields = new Field[refSize];
        refFields = fieldsToReset.toArray(refFields);
        System.arraycopy(refFields, 0, fields, 0, refSize);
      }
      if (resetSize != 0) {
        Field[] resetFields = new Field[resetSize];
        resetFields = fieldsToReset.toArray(resetFields);
        System.arraycopy(resetFields, refSize, fields, refSize, resetSize);
      }

      return fields;
    }

    public void addField(Field f, boolean changedAccessibility) {
      if (fieldsToReset.contains(f) || refFields.contains(f)) {
        return;
      }

      if (changedAccessibility) {
        fieldsToReset.add(f);
      } else {
        refFields.add(f);
      }
    }
  }
}
