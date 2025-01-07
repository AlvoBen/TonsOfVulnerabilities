package com.sap.sdo.impl.objects.strategy.enhancer;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sap.engine.library.bytecode.cf.AnnotationAttribute;
import com.sap.engine.library.bytecode.cf.AnnotationPairValue;
import com.sap.engine.library.bytecode.cf.AnnotationStruct;
import com.sap.engine.library.bytecode.cf.Attributed;
import com.sap.engine.library.bytecode.cf.CFFactory;
import com.sap.engine.library.bytecode.cf.CPInfo;
import com.sap.engine.library.bytecode.cf.ClassFile;
import com.sap.engine.library.bytecode.cf.CodeAttribute;
import com.sap.engine.library.bytecode.cf.Constants;
import com.sap.engine.library.bytecode.cf.Instruction;
import com.sap.engine.library.bytecode.cf.LocalVariable;
import com.sap.engine.library.bytecode.cf.LocalVariableTableAttribute;
import com.sap.engine.library.bytecode.cf.MethodInfo;
import com.sap.engine.library.bytecode.misc.StringUtils;
import com.sap.sdo.impl.objects.DataObjectDecorator;

/**
 * @author Ron Barack
 */
final public  class PojoClassEnhancer {
    public static <T> Class<T> createEnhancedClass(Class<T> clz) throws IOException, ClassNotFoundException {
        return generateEnhancedClass(new ReflectedClassMetadata(clz),false);
    }
    private static interface IAnnotationMetadata {
        String getType();
        NameValuePair[] getNameValuePairs();
    }
    private static interface IClassMetadata {
        String getName();
        IMethodMetadata[] getMethods();
        TypeInfo getPropertyType();
        IAnnotationMetadata[] getAnnotations();
        ClassLoader getClassLoader();
        InputStream getInputStream();
    }
    private static interface IMethodMetadata {
        String getName();
        IClassMetadata getReturnType();
        IClassMetadata[] getParameterTypes();
        String getMethodDescriptor();
        boolean ignore();
        IAnnotationMetadata[] getAnnotations();
    }
//    private static class BytecodeClassMetatData implements IClassMetadata {
//        private final ClassFile _cf;
//        private IMethodMetadata[] _methods;
//        public BytecodeClassMetatData(ClassFile cf) {
//            _cf = cf;
//        }
//        public IMethodMetadata[] getMethods() {
//            if (_methods == null) {
//                fillMethods();
//            }
//            return _methods;
//        }
//        private void fillMethods() {
//            _methods = new IMethodMetadata[_cf.getMethodsArray().length];
//            for (int i=0; i<_cf.getMethodsArray().length; i++) {
//                _methods[i] = new BytecodeMethodMetadata(_cf.getMethodsArray()[i]);
//            }
//        }
//
//        public String getName() {
//            return _cf.getName();
//        }
//
//        public TypeInfo getPropertyType() {
//            TypeInfo ret = new TypeInfo();
//            ret.baseTypeString = "L"+getName()+";";
//            ret.wrapperClassName = getName();
//            return ret;
//        }
//        public IAnnotationMetadata[] getAnnotations() {
//            // TODO Auto-generated method stub
//            return null;
//        }
//        public ClassLoader getClassLoader() {
//            return Thread.currentThread().getContextClassLoader();
//        }
//        public InputStream getInputStream() {
//            // TODO Auto-generated method stub
//            throw new RuntimeException("not yet implemented");
//        }
//    }
//    private static class BytecodeReferencedClassMetadata implements IClassMetadata {
//        private TypeInfo _typeInfo;
//        public BytecodeReferencedClassMetadata(String s) {
//            _typeInfo = PrimitiveTypeInfo.getReverse(s);
//            if (_typeInfo == null) {
//                _typeInfo = new TypeInfo();
//                _typeInfo.baseTypeString = s;
//                _typeInfo.wrapperClassName = s.substring(1, s.length()-1);
//            }
//        }
//        public IMethodMetadata[] getMethods() {
//            return null;
//        }
//
//        public String getName() {
//            return _typeInfo.wrapperClassName;
//        }
//
//        public TypeInfo getPropertyType() {
//            return _typeInfo;
//        }
//        public IAnnotationMetadata[] getAnnotations() {
//            // TODO Auto-generated method stub
//            return null;
//        }
//        public ClassLoader getClassLoader() {
//            return Thread.currentThread().getContextClassLoader();
//        }
//        public InputStream getInputStream() {
//            // TODO Auto-generated method stub
//            throw new RuntimeException("not yet implemented");
//        }
//    }
    private static class ReflectedClassMetadata implements IClassMetadata {
        private final Class _class;
        private IMethodMetadata[] _methods;
        public ClassLoader getClassLoader() {
            return _class.getClassLoader();
        }
        public ReflectedClassMetadata(Class clazz) {
            _class = clazz;
        }
        public IMethodMetadata[] getMethods() {
            if (_methods == null) {
                fillMethods();
            }
            return _methods;
        }
        private void fillMethods() {
            _methods = new IMethodMetadata[_class.getMethods().length];
            for (int i=0; i<_class.getMethods().length; i++) {
                _methods[i] = new ReflectedMethodMetadata(_class.getMethods()[i]);
            }
        }
        public String getName() {
            return _class.getName().replace('.', '/');
        }
        public TypeInfo getPropertyType() {
            String fieldType = getFieldType(_class);
            if (fieldType.startsWith("L")) {
                TypeInfo ret = new TypeInfo();
                ret.baseTypeString = fieldType;
                ret.wrapperClassName = fieldType.substring(1, fieldType.length()-1);
                return ret;
            }
            return PrimitiveTypeInfo.getReverse(fieldType);
        }
        public IAnnotationMetadata[] getAnnotations() {
            IAnnotationMetadata[] annotations = new IAnnotationMetadata[_class.getAnnotations().length];
            for (int i=0; i<annotations.length; i++) {
                annotations[i] = new ReflectedAnnotationMetadata(_class.getAnnotations()[i]);
            }
            return annotations;
        }
        public InputStream getInputStream() {
            return getClassLoader().getResourceAsStream(StringUtils.fqNameToShortName(_class) + ".class");
        }
    }
    private static class NameValuePair {
        String _name;
        String _type;
        Object _value;
        public NameValuePair(String name, String type, Object object) {
            _name = name;
            _type = type;
            _value = object;
        }
        public String getName() {
            return _name;
        }
        public String getType() {
            return _type;
        }
        public Object getValue() {
            return _value;
        }
        public int getTag() {
            if (_type.equals("Ljava/lang/String;")) {
                return Constants.C_STRING;
            }
            return _type.charAt(0);
        }
        public CPInfo getValueCPInfo(ClassFile cf) {
            switch (getTag()) {
            case Constants.C_BOOLEAN:
                return cf.createCPInfoInteger(((Boolean)_value?1:0));
            case Constants.C_STRING:
                return cf.createCPInfoUtf8((String)_value);
            default:
                return null;
            }
        }
    }
    private static class ReflectedAnnotationMetadata implements IAnnotationMetadata {
        private final Annotation _annotation;
        public ReflectedAnnotationMetadata(Annotation annotation) {
            _annotation = annotation;
        }
        public NameValuePair[] getNameValuePairs() {
            Class annotationClass = _annotation.annotationType();
            // TODO:  Handle Inheritance
            List<NameValuePair> ret = new ArrayList<NameValuePair>();
            for (int i=0; i<annotationClass.getMethods().length; i++) {
                if (annotationClass.getMethods()[i].getParameterTypes().length>0) {
                    continue;
                }
                if (annotationClass.getMethods()[i].getDeclaringClass().equals(Object.class)) {
                    continue;
                }
                if (annotationClass.getMethods()[i].getDeclaringClass().equals(Annotation.class)) {
                    continue;
                }
                // TODO Handle other types
                if (!annotationClass.getMethods()[i].getReturnType().equals(String.class)
                        && !annotationClass.getMethods()[i].getReturnType().equals(boolean.class)) {
                    continue;
                }
                try {
                    NameValuePair nvp = new NameValuePair(annotationClass.getMethods()[i].getName(),
                            PojoClassEnhancer.getFieldType(annotationClass.getMethods()[i].getReturnType()),
                            annotationClass.getMethods()[i].invoke(_annotation));
                    if (nvp != null) {
                        ret.add(nvp);
                    }
                } catch (SecurityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return ret.toArray(new NameValuePair[ret.size()]);
        }
        public String getType() {
            return "L"+_annotation.annotationType().getName().replace('.', '/')+';';
        }
    }
    private static class ReflectedMethodMetadata implements IMethodMetadata {
        private final Method _method;
        private final IClassMetadata _returnType;
        private final IClassMetadata[] _parameters;
        public ReflectedMethodMetadata(Method m) {
            _method = m;
            if (m.getReturnType()!=null && !m.getReturnType().equals(void.class)) {
                _returnType = new ReflectedClassMetadata(m.getReturnType());
            } else {
                _returnType = null;
            }
            _parameters = new IClassMetadata[_method.getParameterTypes().length];
            for (int i=0; i<_method.getParameterTypes().length; i++) {
                _parameters[i] = new ReflectedClassMetadata(_method.getParameterTypes()[i]);
            }
        }
        public String getMethodDescriptor() {
            return PojoClassEnhancer.getMethodDescriptor(_method.getParameterTypes(), _method.getReturnType());
        }
        public String getName() {
            return _method.getName();
        }
        public IClassMetadata[] getParameterTypes() {
            return _parameters;
        }
        public IClassMetadata getReturnType() {
            return _returnType;
        }
        public boolean ignore() {
            return _method.getDeclaringClass().equals(Object.class);
        }
        public IAnnotationMetadata[] getAnnotations() {
            IAnnotationMetadata[] annotations = new IAnnotationMetadata[_method.getAnnotations().length];
            for (int i=0; i<annotations.length; i++) {
                annotations[i] = new ReflectedAnnotationMetadata(_method.getAnnotations()[i]);
            }
            return annotations;
        }
    }
//    private static class BytecodeMethodMetadata implements IMethodMetadata {
//        private final MethodInfo _method;
//        private final IClassMetadata _returnType;
//        private final IClassMetadata[] _parameters;
//        public BytecodeMethodMetadata(MethodInfo m) {
//            _method = m;
//            if (m.getReturnType()!=null && !m.getReturnType().equals(void.class)) {
//                _returnType = new BytecodeReferencedClassMetadata(m.getReturnType());
//            } else {
//                _returnType = null;
//            }
//            _parameters = new IClassMetadata[_method.getArguments().length];
//            for (int i=0; i<_method.getArguments().length; i++) {
//                _parameters[i] = new BytecodeReferencedClassMetadata(_method.getArguments()[i]);
//            }
//        }
//        public String getMethodDescriptor() {
//            return _method.getDescriptor();
//        }
//        public String getName() {
//            return _method.getName();
//        }
//        public IClassMetadata[] getParameterTypes() {
//            return _parameters;
//        }
//        public IClassMetadata getReturnType() {
//            return _returnType;
//        }
//        public boolean ignore() {
//            return false;
//        }
//        public IAnnotationMetadata[] getAnnotations() {
//            // TODO Auto-generated method stub
//            return null;
//        }
//
//    }
      /**
       * Return the "method descriptor" string for a method with the given
       * parameter types and return type.  See JVMS section 4.3.3.
       */
      private static String getMethodDescriptor(Class[] parameterTypes,
                          Class returnType)
      {
    return getParameterDescriptors(parameterTypes) +
        ((returnType == void.class) ? "V" : getFieldType(returnType));
      }
      /**
       * Return the list of "parameter descriptor" strings enclosed in
       * parentheses corresponding to the given parameter types (in other
       * words, a method descriptor without a return descriptor).  This
       * string is useful for constructing string keys for methods without
       * regard to their return type.
       */
      private static String getParameterDescriptors(Class[] parameterTypes) {
    StringBuilder desc = new StringBuilder("(");
    for (int i = 0; i < parameterTypes.length; i++) {
        desc.append(getFieldType(parameterTypes[i]));
    }
    desc.append(')');
    return desc.toString();
      }
    public static byte[] generateEnhancedBytecode(String originalClassPath, String enhancedClassPath, IClassMetadata pojoClass, boolean generateGetters) throws IOException, ClassNotFoundException {
        final CFFactory factory = CFFactory.getThreadLocalInstance();

        final ClassFile cf = factory.createClassFile();
        cf.setPublic(true);
        cf.setSuperClass(originalClassPath);
        cf.setVersion(49, 0);
        cf.addInterface(cf.createCPInfoClass("com/sap/sdo/impl/objects/DataObjectDecorator"));
        cf.addInterface(cf.createCPInfoClass("com/sap/sdo/impl/objects/strategy/enhancer/WrapsDataStrategy"));
        cf.setThisClass(enhancedClassPath);
        cf.createField("_ds", "Lcom/sap/sdo/impl/objects/DataStrategy;");
        CPInfo thisCPInfo = cf.createCPInfoUtf8("this");
        CPInfo classNameCPInfo = cf.createCPInfoUtf8("L" + cf.getName() + ";");
        CPInfo info = cf.createCPInfoMemberRef(Constants.CONSTANT_Fieldref, cf.getName(), "_ds", "Lcom/sap/sdo/impl/objects/DataStrategy;");
        generateConstructor(originalClassPath, factory, cf, thisCPInfo, classNameCPInfo);
        generateGetDSMethod(originalClassPath, factory, cf, thisCPInfo, classNameCPInfo);
        Set<String> allMethods = new HashSet<String>();
        for (int i=0; i<pojoClass.getMethods().length; i++) {
            IMethodMetadata beanMethod = pojoClass.getMethods()[i];
            if (beanMethod.ignore()) {
                continue;
            }
            if (isGetter(beanMethod) && generateGetters) {
                String methodDescr = beanMethod.getMethodDescriptor();
                allMethods.add(beanMethod.getName()+methodDescr);
                TypeInfo typeInfo = beanMethod.getReturnType().getPropertyType();
                generateGetter(factory, cf, beanMethod, typeInfo);
                generateInternalGetter(factory, cf, beanMethod, typeInfo, originalClassPath);
            } else if (isSetter(beanMethod)) {
                String methodDescr = beanMethod.getMethodDescriptor();
                allMethods.add(beanMethod.getName()+methodDescr);
                TypeInfo typeInfo = beanMethod.getParameterTypes()[0].getPropertyType();
                generateSetter(factory, cf, beanMethod, typeInfo);
                generateInternalSetter(factory, cf, beanMethod, typeInfo, originalClassPath);
            }
        }
        for (int i=0; i<DataObjectDecorator.class.getMethods().length; i++) {
            generateDelegatorMethod(allMethods, factory, cf, thisCPInfo, classNameCPInfo, info, DataObjectDecorator.class.getMethods()[i]);
        }

        IAnnotationMetadata[] classAnnotations = pojoClass.getAnnotations();
        addAnnotations(cf, cf, classAnnotations);

        return cf.serializeToByteArray();
    }
    private static void generateGetDSMethod(String originalClassPath, CFFactory factory, ClassFile cf, CPInfo thisCPInfo, CPInfo classNameCPInfo) {
        MethodInfo m = cf.createMethod("_getDataStrategy","()Lcom/sap/sdo/impl/objects/DataStrategy;");
        m.setPublic(true);
        CodeAttribute ca = m.createCodeAttribute();
        ca.setMaxLocals(1);
        ca.setOriginalMaxStack(1);
        LocalVariableTableAttribute vars = ca.createLocalVariableTableAttribute();
        LocalVariable var0 = factory.createLocalVariable(0);
        vars.addEntry("L0", "L1", cf.createCPInfoUtf8("this"), cf.createCPInfoUtf8("L" + cf.getName() + ";"), var0);
        Instruction[] x = new Instruction[20];
        int nx = 0;
        x[nx++] = ca.createALOAD(var0);
        CPInfo info = cf.createCPInfoMemberRef(Constants.CONSTANT_Fieldref, cf.getName(), "_ds", "Lcom/sap/sdo/impl/objects/DataStrategy;");
        x[nx++] = ca.createGetField(info);
        x[nx++] = ca.createRETURN();
        ca.insert(x, 0, nx, 0);
        x[0].addLabel("L0");
        x[nx - 1].addLabel("L1");

    }
    private static void addAnnotations(final ClassFile cf, final Attributed obj, IAnnotationMetadata[] annotations) {
        if (annotations.length>0) {
            AnnotationAttribute att = new AnnotationAttribute(Constants.S_RUNNTIME_VISIBLE_ANNOTATION,annotations.length,obj);
            for (int i=0; i<annotations.length; i++) {
                AnnotationStruct struct = new AnnotationStruct(cf.createCPInfoUtf8(annotations[i].getType()));
                for (int j=0; j<annotations[i].getNameValuePairs().length; j++) {
                    NameValuePair pair = annotations[i].getNameValuePairs()[j];
                    if (pair == null) {
                        continue;
                    }
                    // TODO:  Handle non-primitive values!
                    AnnotationPairValue value = new AnnotationPairValue(pair.getTag());
                    value.setConstant(pair.getValueCPInfo(cf));
                    value.setNameIdx(cf.createCPInfoUtf8(pair.getName()));
                    struct.addElementValuePair(pair.getName(), value);
                }
                att.addAnnotation(struct);
            }
            obj.addAttribute(att);
        }
    }

    public static Class generateEnhancedClass(IClassMetadata pojoClass, boolean generateGetters) throws IOException, ClassNotFoundException {
        final String originalClassPath = pojoClass.getName();
        final String enhancedClassPath = originalClassPath + "$EnhancedBySDO";
        final String enhancedClassName = enhancedClassPath.replace('/', '.');
        final byte[] data = generateEnhancedBytecode(originalClassPath, enhancedClassPath, pojoClass, generateGetters);
        ClassLoader loader = new ClassLoader(pojoClass.getClassLoader()) {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                if (name.equals(enhancedClassName)) {
                    return defineClass(name, data, 0, data.length);
                } else {
                    return getParent().loadClass(name);
                }
            }
        };
        return loader.loadClass(enhancedClassName);
    }

    private static boolean isGetter(IMethodMetadata beanMethod) {
        if (beanMethod.getReturnType()==null || beanMethod.getReturnType().equals(void.class)) {
            return false;
        }
        if (beanMethod.getParameterTypes().length>0) {
            return false;
        }
        if (beanMethod.getName().startsWith("get")) {
            return true;
        }
        boolean b = beanMethod.getReturnType().equals(Boolean.class) ||
            beanMethod.getReturnType().equals(boolean.class);
        if (!b) {
            return false;
        }
        if (beanMethod.getName().startsWith("is")) {
            return true;
        }
        return false;
    }
    private static boolean isSetter(IMethodMetadata beanMethod) {
        if (beanMethod.getReturnType()!=null && !beanMethod.getReturnType().equals(void.class)) {
            return false;
        }
        if (beanMethod.getParameterTypes().length!=1) {
            return false;
        }
        if (beanMethod.getName().startsWith("set")) {
            return true;
        }
        return false;
    }
    private static void generateConstructor(final String originalClassPath, final CFFactory factory, final ClassFile cf, CPInfo thisCPInfo, CPInfo classNameCPInfo) {
        MethodInfo ctr = cf.createMethod("<init>", "(Lcom/sap/sdo/impl/objects/DataStrategy;)V");
        ctr.setPublic(true);
        CodeAttribute ca = ctr.createCodeAttribute();
        LocalVariableTableAttribute vars = ca.createLocalVariableTableAttribute();
        LocalVariable var0 = factory.createLocalVariable(0);
        vars.addEntry("L0", "L1", thisCPInfo, classNameCPInfo, var0);
        LocalVariable var1 = factory.createLocalVariable(1);
        vars.addEntry("L0", "L1", cf.createCPInfoUtf8("arg0"), cf.createCPInfoUtf8("Lcom/sap/sdo/impl/objects/DataStrategy;"), var1);
        int nx = 0;
        Instruction[] x = new Instruction[100];
        x[nx++] = ca.createALOAD(var0);
        x[nx++] = ca.createInvokeSpecial(originalClassPath, "<init>", "()V");
        x[nx++] = ca.createALOAD(var0);
        x[nx++] = ca.createALOAD(var1);
        x[nx++] = ca.createPutField(cf.getName(), "_ds", "Lcom/sap/sdo/impl/objects/DataStrategy;");
        x[nx++] = ca.createRETURN();
        ca.insert(x, 0, nx, 0);
        x[0].addLabel("L0");
        x[nx - 1].addLabel("L1");
        ca.setMaxLocals(2);
        ca.setOriginalMaxStack(2);
    }

    private static void generateDelegatorMethod(Set<String> allMethods, final CFFactory factory, final ClassFile cf, CPInfo thisCPInfo, CPInfo classNameCPInfo, CPInfo info, Method templateMethod) {
        String methodDescr = getMethodDescriptor(templateMethod.getParameterTypes(), templateMethod.getReturnType());
        if (allMethods.contains(templateMethod.getName()+methodDescr)) {
            return;
        }
        MethodInfo m = cf.createMethod(templateMethod.getName(), methodDescr);
        m.setPublic(true);
        CodeAttribute ca = m.createCodeAttribute();
        LocalVariableTableAttribute vars = ca.createLocalVariableTableAttribute();
        LocalVariable var0 = factory.createLocalVariable(0);
        vars.addEntry("L0", "L1", thisCPInfo, classNameCPInfo, var0);
        int nParams = templateMethod.getParameterTypes().length;
        int nx = 0;
        Instruction[] x = new Instruction[100];
        x[nx++] = ca.createALOAD(var0);
        x[nx++] = ca.createGetField(info);
        x[nx++] = ca.createInvokeInterface("com/sap/sdo/impl/objects/DataStrategy","getDataObject","()Lcom/sap/sdo/impl/objects/GenericDataObject;");
        for (int j=0; j<nParams; j++) {
            LocalVariable var = factory.createLocalVariable(j+1);
            String fieldType = getFieldType(templateMethod.getParameterTypes()[j]);
            vars.addEntry("L0", "L1", cf.createCPInfoUtf8("arg"+j), cf.createCPInfoUtf8(fieldType), var);
            x[nx++] = ca.createLOAD(var, fieldType);
        }
        x[nx++] = ca.createInvokeVirtual("com/sap/sdo/impl/objects/GenericDataObject", m.getName(), m.getDescriptor());
        x[nx++] = ca.createRETURN();
        ca.insert(x, 0, nx, 0);
        x[0].addLabel("L0");
        x[nx - 1].addLabel("L1");
        ca.setMaxLocals(nParams+1);
        ca.setOriginalMaxStack(nParams+1);
    }

      /**
       * A PrimitiveTypeInfo object contains assorted information about
       * a primitive type in its public fields.  The struct for a particular
       * primitive type can be obtained using the static "get" method.
       */
      private static class PrimitiveTypeInfo extends TypeInfo {

    private static Map<Class,PrimitiveTypeInfo> table =
        new HashMap<Class,PrimitiveTypeInfo>();
    private static Map<String,PrimitiveTypeInfo> rev =
        new HashMap<String,PrimitiveTypeInfo>();
    static {
        add(byte.class, Byte.class);
        add(char.class, Character.class);
        add(double.class, Double.class);
        add(float.class, Float.class);
        add(int.class, Integer.class);
        add(long.class, Long.class);
        add(short.class, Short.class);
        add(boolean.class, Boolean.class);
    }

    private static void add(Class primitiveClass, Class wrapperClass) {
        PrimitiveTypeInfo ret = new PrimitiveTypeInfo(primitiveClass, wrapperClass);
        table.put(primitiveClass,ret);
        rev.put(ret.baseTypeString, ret);
    }

    private PrimitiveTypeInfo(Class primitiveClass, Class wrapperClass) {
        assert primitiveClass.isPrimitive();

        baseTypeString =
        Array.newInstance(primitiveClass, 0)
        .getClass().getName().substring(1);
        wrapperClassName = wrapperClass.getName().replace('.', '/');
        wrapperValueOfDesc =
        "(" + baseTypeString + ")L" + wrapperClassName + ";";
        unwrapMethodName = primitiveClass.getName() + "Value";
        unwrapMethodDesc = "()" + baseTypeString;
    }

    public static PrimitiveTypeInfo get(Class cl) {
        return table.get(cl);
    }
    public static PrimitiveTypeInfo getReverse(String s) {
        return rev.get(s);
    }
      }


      /**
       * Return the "field type" string for the given type, appropriate for
       * a field descriptor, a parameter descriptor, or a return descriptor
       * other than "void".  See JVMS section 4.3.2.
       */
      private static String getFieldType(Class type) {
    if (type.isPrimitive()) {
        return PrimitiveTypeInfo.get(type).baseTypeString;
    } else if (type.isArray()) {
        /*
         * According to JLS 20.3.2, the getName() method on Class does
         * return the VM type descriptor format for array classes (only);
         * using that should be quicker than the otherwise obvious code:
         *
         *     return "[" + getTypeDescriptor(type.getComponentType());
         */
        return type.getName().replace('.', '/');
    } else {
        return "L" + type.getName().replace('.', '/') + ";";
    }
      }

      private static class TypeInfo {
        /** "base type" used in various descriptors (see JVMS section 4.3.2) */
        public String baseTypeString;

        /** name of corresponding wrapper class */
        public String wrapperClassName;

        /** method descriptor for wrapper class "valueOf" factory method */
        public String wrapperValueOfDesc;

        /** name of wrapper class method for retrieving primitive value */
        public String unwrapMethodName;

        /** descriptor of same method */
        public String unwrapMethodDesc;
      }
    private static void generateGetter(CFFactory factory, ClassFile cf, IMethodMetadata beanMethod, TypeInfo typeInfo) {
        MethodInfo m = cf.createMethod(beanMethod.getName(),"()"+typeInfo.baseTypeString);

        CodeAttribute ca = m.createCodeAttribute();
        ca.setMaxLocals(1);
        ca.setOriginalMaxStack(3);
        LocalVariableTableAttribute vars = ca.createLocalVariableTableAttribute();
        LocalVariable var0 = factory.createLocalVariable(0);
        vars.addEntry("L0", "L1", cf.createCPInfoUtf8("this"), cf.createCPInfoUtf8("L" + cf.getName() + ";"), var0);
        Instruction[] x = new Instruction[20];
        int nx = 0;
        x[nx++] = ca.createALOAD(var0);
        x[nx++] = ca.createALOAD(var0);
        CPInfo info = cf.createCPInfoMemberRef(Constants.CONSTANT_Fieldref, cf.getName(), "_ds", "Lcom/sap/sdo/impl/objects/DataStrategy;");
        x[nx++] = ca.createGetField(info);
        x[nx++] = ca.createLDC(m.getName());
        x[nx++] = ca.createLDC(m.getDescriptor());
        x[nx++] = ca.createInvokeStatic("com/sap/sdo/impl/objects/strategy/enhancer/DefaultPojoHelper", "get", "(Ljava/lang/Object;Lcom/sap/sdo/impl/objects/DataStrategy;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Object;");
        x[nx++] = ca.createCHECKCAST(typeInfo.wrapperClassName);
        if (typeInfo.unwrapMethodName!=null) {
            x[nx++] = ca.createInvokeVirtual(typeInfo.wrapperClassName,typeInfo.unwrapMethodName,typeInfo.unwrapMethodDesc);
        }
        x[nx++] = ca.createRETURN();
        ca.insert(x, 0, nx, 0);
        x[0].addLabel("L0");
        x[nx - 1].addLabel("L1");

        addAnnotations(cf, m, beanMethod.getAnnotations());
    }

    private static void generateInternalGetter(CFFactory factory, ClassFile cf, IMethodMetadata beanMethod, TypeInfo typeInfo, String className) {
        MethodInfo m = cf.createMethod('_'+beanMethod.getName(),"()"+typeInfo.baseTypeString);
        m.setPublic(true);
        CodeAttribute ca = m.createCodeAttribute();
        ca.setMaxLocals(1);
        ca.setOriginalMaxStack(1);
        LocalVariableTableAttribute vars = ca.createLocalVariableTableAttribute();
        LocalVariable var0 = factory.createLocalVariable(0);
        vars.addEntry("L0", "L1", cf.createCPInfoUtf8("this"), cf.createCPInfoUtf8("L" + cf.getName() + ";"), var0);
        Instruction[] x = new Instruction[20];
        int nx = 0;
        x[nx++] = ca.createALOAD(var0);
        x[nx++] = ca.createInvokeSpecial(className, beanMethod.getName(), "()"+typeInfo.baseTypeString);
        x[nx++] = ca.createRETURN();
        ca.insert(x, 0, nx, 0);
        x[0].addLabel("L0");
        x[nx - 1].addLabel("L1");
    }
    private static void generateSetter(CFFactory factory, ClassFile cf, IMethodMetadata beanMethod, TypeInfo typeInfo) {
        MethodInfo m = cf.createMethod(beanMethod.getName(),"("+typeInfo.baseTypeString+")V");

        CodeAttribute ca = m.createCodeAttribute();
        ca.setMaxLocals(2);
        ca.setOriginalMaxStack(5);
        LocalVariableTableAttribute vars = ca.createLocalVariableTableAttribute();
        LocalVariable var0 = factory.createLocalVariable(0);
        vars.addEntry("L0", "L1", cf.createCPInfoUtf8("this"), cf.createCPInfoUtf8("L" + cf.getName() + ";"), var0);
        LocalVariable var1 = factory.createLocalVariable(1);
        vars.addEntry("L0", "L1", cf.createCPInfoUtf8("arg0"), cf.createCPInfoUtf8(typeInfo.baseTypeString), var1);

        Instruction[] x = new Instruction[20];
        int nx = 0;
        x[nx++] = ca.createALOAD(var0);
        x[nx++] = ca.createALOAD(var0);
        CPInfo info = cf.createCPInfoMemberRef(Constants.CONSTANT_Fieldref, cf.getName(), "_ds", "Lcom/sap/sdo/impl/objects/DataStrategy;");
        x[nx++] = ca.createGetField(info);
        x[nx++] = ca.createLDC(m.getName());
        x[nx++] = ca.createLDC(m.getDescriptor());
        x[nx++] = ca.createLOAD(var1, typeInfo.baseTypeString);
        if (typeInfo.wrapperValueOfDesc!=null) {
            x[nx++] = ca.createInvokeStatic(typeInfo.wrapperClassName,"valueOf",typeInfo.wrapperValueOfDesc);
        }
        x[nx++] = ca.createInvokeStatic("com/sap/sdo/impl/objects/strategy/enhancer/DefaultPojoHelper", "set", "(Ljava/lang/Object;Lcom/sap/sdo/impl/objects/DataStrategy;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V");
        x[nx++] = ca.createRETURN();
        ca.insert(x, 0, nx, 0);
        x[0].addLabel("L0");
        x[nx - 1].addLabel("L1");

        addAnnotations(cf, m, beanMethod.getAnnotations());
    }
    private static void generateInternalSetter(CFFactory factory, ClassFile cf, IMethodMetadata beanMethod, TypeInfo typeInfo, String className) {
        MethodInfo m = cf.createMethod('_'+beanMethod.getName(),"("+typeInfo.baseTypeString+")V");
        m.setPublic(true);
        CodeAttribute ca = m.createCodeAttribute();
        ca.setMaxLocals(2);
        ca.setOriginalMaxStack(2);
        LocalVariableTableAttribute vars = ca.createLocalVariableTableAttribute();
        LocalVariable var0 = factory.createLocalVariable(0);
        vars.addEntry("L0", "L1", cf.createCPInfoUtf8("this"), cf.createCPInfoUtf8("L" + cf.getName() + ";"), var0);
        LocalVariable var1 = factory.createLocalVariable(1);
        vars.addEntry("L0", "L1", cf.createCPInfoUtf8("arg0"), cf.createCPInfoUtf8(typeInfo.baseTypeString), var1);

        Instruction[] x = new Instruction[20];
        int nx = 0;
        x[nx++] = ca.createALOAD(var0);
        x[nx++] = ca.createLOAD(var1, typeInfo.baseTypeString);
        x[nx++] = ca.createInvokeSpecial(className, beanMethod.getName(), "("+typeInfo.baseTypeString+")V");
        x[nx++] = ca.createRETURN();
        ca.insert(x, 0, nx, 0);
        x[0].addLabel("L0");
        x[nx - 1].addLabel("L1");
    }
}
