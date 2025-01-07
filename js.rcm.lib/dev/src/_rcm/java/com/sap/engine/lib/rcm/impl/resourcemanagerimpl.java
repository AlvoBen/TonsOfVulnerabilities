package com.sap.engine.lib.rcm.impl;

import com.sap.engine.lib.rcm.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.lang.ref.WeakReference;


/**
 * Created by Asen Petrov.
 * IUser: I030789
 * Date: 2007-12-6
 * Time: 17:11:11
 */
public class ResourceManagerImpl implements ResourceManager {

  private HashMap<String, ResourceProvider> resources =  new HashMap<String, ResourceProvider>(2);
  private HashSet<ResourceContextImpl> inactiveContexts = new HashSet<ResourceContextImpl>(2);
  private ConsumerTypeEntry[] consumerTypes = new ConsumerTypeEntry[0];


  public synchronized void registerResource(ResourceProvider provider) {
    Resource resource = provider.getResource();
    String name = resource.getName();
    if (resources.get(name) != null)  {
      throw new IllegalStateException("Resource " + name + " is already registered");
    } else {
      resources.put(name, provider);
    }
    updateConfiguration(resource, true);
  }

  public synchronized void unregisterResource(ResourceProvider provider) {
    Resource resource = provider.getResource();
    String name = resource.getName();
    if (resources.get(name) == null)  {
      throw new IllegalStateException("Resource " + name + " is not registered");
    } else {
      resources.remove(name);
    }
    updateConfiguration(resource, false);
  }

  public synchronized ResourceContextImpl createResourceContext(String resourceName, String consumerType) {
     ResourceContextImpl context = new ResourceContextImpl(resourceName, consumerType);
     addResourceContext(context);
     return context;
  }

  public synchronized ResourceContext getResourceContext(ResourceConsumer consumer, String resourceName) {
    String type = consumer.getType();
    for (ConsumerTypeEntry typeEntry :consumerTypes)  {
      if (typeEntry.type.equals(type)) {
        for (ResourceContextImpl ctx : typeEntry.contexts) {
          if (ctx.getResourceName().equals(resourceName)) {
            return ctx;
          }
        }
      }
    }
    for (ResourceContextImpl inactiveContext : inactiveContexts) {
      if (inactiveContext.getConsumerType().equals(type) && inactiveContext.getResourceName().equals(resourceName)) {
        return inactiveContext;
      }
    }
    return null;
  }

  public synchronized ResourceContext[] getInactiveResourceContexts(ResourceConsumer consumer) {
    HashSet<ResourceContext> set = new HashSet<ResourceContext>();
    String type = consumer.getType();
    for (ResourceContextImpl inactiveContext : inactiveContexts) {
      if (inactiveContext.getConsumerType().equals(type)) {
        set.add(inactiveContext);
      }
    }
    return set.toArray(new ResourceContext[set.size()]);
  }

  public synchronized ResourceContext[] getActiveResourceContexts(ResourceConsumer consumer) {
    for (ConsumerTypeEntry typeEntry :consumerTypes)  {
      if (typeEntry.type.equals(consumer.getType())) {
        return typeEntry.contexts.clone();
      }
    }
    return new ResourceContext[0];
  }

  private void addResourceContext(ResourceContextImpl context) {
    String consumerType = context.getConsumerType();
    String resourceName = context.getResourceName();
    for (ConsumerTypeEntry typeEntry :consumerTypes)  {
      for(ResourceContextImpl c: typeEntry.contexts) {
         if (c.getConsumerType().equals(consumerType) && c.getResourceName().equals(resourceName)) {
           throw new IllegalStateException("ResourceContextImpl for resource " + resourceName + " and consumer type " + consumerType + " already exists" );
         }
      }
    }
    for(ResourceContextImpl c:inactiveContexts) {
       if (c.getConsumerType().equals(consumerType) && context.getResourceName().equals(resourceName)) {
         throw new IllegalStateException("ResourceContextImpl for resource " + resourceName + " and consumer type " + consumerType + " already exists" );
       }
     }
     ResourceProvider provider  = resources.get(resourceName);
     if (provider != null) {
      Constraint constraint  = provider.getDefaultConstrait();
      if (constraint != null) {
        context.addConstraint(constraint);
      }
      Notification notification = provider.getDefaultNotification();
      if (notification != null) {
        context.addNotification(notification);
      }
      context.resource = provider.getResource();
      activateContext(context);
    } else {
      inactiveContexts.add(context);
    }
  }

  public synchronized void destroyResourceContext(ResourceContext context) {
     if (!inactiveContexts.remove(context)) {
      ArrayList<ConsumerTypeEntry> newTypes = new ArrayList<ConsumerTypeEntry>(consumerTypes.length);
      for (ConsumerTypeEntry typeEntry :consumerTypes)  {
       for(int i = 0; i < typeEntry.contexts.length; i++) {
         if (typeEntry.contexts[i].equals(context)) {
           typeEntry.contexts[i].setActive(false);
           if (typeEntry.contexts.length != 1) {
             ResourceContextImpl[] temp = new ResourceContextImpl[typeEntry.contexts.length - 1];
             System.arraycopy(typeEntry.contexts, 0, temp, 0, i);
             System.arraycopy(typeEntry.contexts, i + 1, temp, 0, typeEntry.contexts.length - i);
             typeEntry.contexts = temp;
             newTypes.add(typeEntry);
           }
           break;
         }
       }
     }
     consumerTypes = newTypes.toArray(new ConsumerTypeEntry[newTypes.size()]);
    } //TODO optimize empty consumer type removal 
  }

  public boolean consume(ResourceConsumer consumer, String resourceName, long quantity) {
    for(ConsumerTypeEntry consumerType:consumerTypes) {  // search if there is a configuration for this consumer type
      if (consumerType.type.equals(consumer.getType())) {
        for(ResourceContextImpl resourceContext:consumerType.contexts) { // search if there is a configuration for this resource
          if (resourceContext.getResourceName().equals(resourceName)) {
            long consumed = 0;
            ConsumerCounter consumerCounter = null;
            String id = consumer.getId();
            WeakReference weak = resourceContext.mapTable.get(id);
            if (weak != null) {
                consumerCounter = (ConsumerCounter)weak.get();
            }
            if (weak == null || consumerCounter == null) {
                consumerCounter = resourceContext.getCounter(id);
            }  else {

            }
            consumed = consumerCounter.add(quantity);
            long proposedUsage = consumed + quantity;
            for(Constraint constraint:resourceContext.constraints) {
              if (!constraint.preConsume(consumer, consumed, proposedUsage)) {
                consumerCounter.dec(quantity);
                return false;
              }
            }
            long totalUsage = resourceContext.counter.addAndGet(quantity);
            if (!resourceContext.resource.isUnbounded()  && totalUsage > resourceContext.resource.getTotalQuantity()) {
              consumerCounter.dec(quantity);
              resourceContext.counter.addAndGet(-quantity);
              return false;
            }
            for (Notification notification: resourceContext.notifications) {
              notification.update(consumer, consumed, proposedUsage);
            }
            return true;
          }
        }
        break;
      }
    }
    return true;
  }

  public boolean consume(ResourceConsumer consumer, String[] resourceNames, long[] quantities) {
  /*  for(ConsumerTypeEntry consumerType:consumerTypes) {
      if (consumerType.type.equals(consumer.getType())) {
        ArrayList<ResourceContextImpl> rollbacks = new ArrayList<ResourceContextImpl>();
        boolean rollback = false;
        rollback: for(ResourceContextImpl resourceContext:consumerType.contexts) {
          for (int i = 0; i < resourceNames.length; i++) {
            String resourceName = resourceNames[i];
            if (resourceContext.getResourceName().equals(resourceName)) {
              String id = consumer.getId();
              AtomicLong consumerCounter = resourceContext.counters.get(id);
              if (consumerCounter == null) {
                synchronized(resourceContext.counters) {
                  consumerCounter = resourceContext.counters.get(id);
                  if (consumerCounter == null) {
                    consumerCounter = new AtomicLong();
                    resourceContext.counters.put(id, consumerCounter);
                  }
                }
              }
              long quantity = quantities[i];
              long proposedUsage = consumerCounter.addAndGet(quantity);
              long consumed = proposedUsage - quantity;
              for(Constraint constraint:resourceContext.constraints) {
                if (!constraint.preConsume(consumer, consumed, proposedUsage)) {
                  resourceContext.counter.addAndGet(-quantity);
                  consumerCounter.addAndGet(-quantity);
                  rollback = true;
                  break rollback;
                }
              }
              long totalUsage = resourceContext.counter.addAndGet(quantity);
              if (totalUsage > resourceContext.total) {
                resourceContext.counter.addAndGet(-quantity);
                synchronized(resourceContext) {
                  totalUsage = resourceContext.counter.addAndGet(quantity);
                  if (totalUsage > resourceContext.total) {
                    resourceContext.counter.addAndGet(-quantity);
                    rollback = true;
                    break rollback;
                  }
                }
              }
              rollbacks.add(resourceContext);
            }
          }
        }
        if (rollback)  {
          for (ResourceContextImpl context:rollbacks) {
            for (int i = 0; i < resourceNames.length; i++) {
              if (context.getResourceName().equals(resourceNames[i])) {
                  context.counter.addAndGet(-quantities[i]);
                  context.counters.get(consumer.getId()).addAndGet(-quantities[i]);
                   break;
              }
            }
          }
        } else {
          for( ResourceContextImpl context: rollbacks) {
            for (int i = 0; i < resourceNames.length; i++) {
              if (context.getResourceName().equals(resourceNames[i])) {
                long currentUsage = context.counters.get(consumer.getId()).longValue();
                if (context.notification != null) {
                  context.notification.update(consumer, currentUsage - quantities[i], currentUsage);
                }
                break;
              }
            }
          }
        }
      }
    }
    return true;
    */
    ArrayList<Integer> rollbacks = new ArrayList<Integer>();       // todo optimize
    for (int i = 0; i < resourceNames.length; i++) {
     if (consume(consumer, resourceNames[i], quantities[i])) {
       rollbacks.add(i);
     } else {
       for(int rollback:rollbacks) {
          release(consumer, resourceNames[rollback], quantities[rollback]);
       }
       return false;
     }
    }
    return true;
  }

  public void release(ResourceConsumer consumer, String resourceName, long quantity)  {
    for(ConsumerTypeEntry consumerType:consumerTypes) {  // search if there is a configuration for this consumer type
      if (consumerType.type.equals(consumer.getType())) {
        for(ResourceContextImpl resourceContext:consumerType.contexts) { // search if there is a configuration for this resource
          if (resourceContext.getResourceName().equals(resourceName)) {
             if(!resourceContext.resource.isDisposable()) {
               return;
               //throw new IllegalStateException("The resource " + resourceName + " is not Disposable");
             }
             String id = consumer.getId();
             WeakReference weak = resourceContext.mapTable.get(id);
             if (weak != null) {
                 ConsumerCounter consumerCounter = (ConsumerCounter)weak.get();
                 if (consumerCounter != null) {
                   long left = consumerCounter.dec(quantity);
                   if (left < 0) {
                     throw new IllegalStateException("Consumer " + id + " tries to release " + quantity + " units of resource " + resourceName + " but " + (quantity+left) + " were consumed");
                   }
                   resourceContext.counter.addAndGet(-quantity);
                   for (Notification notification: resourceContext.notifications) {
                     notification.update(consumer, left + quantity, left);
                   }
                   return;
                 }
             }
              break;
          }
        }
      }
    }
    throw new IllegalStateException("Consumer " + consumer.getId() + " is not using  " + quantity + " units of resource " + resourceName);
  }

  public void release(ResourceConsumer consumer, String[] resourceNames, long[] quantities)  {
    for (int i = 0; i < resourceNames.length; i++) {
      release(consumer, resourceNames[i], quantities[i]);
    }

  }

  public void reclaimResource(ResourceConsumer consumer, String resourceName,  long quantity) {
    //log that it is a leak ..
    release(consumer, resourceName, quantity);
  }

  private void updateConfiguration(Resource resource, boolean add) {
    if (add) {
      Iterator i = inactiveContexts.iterator();
      while (i.hasNext()) {
        ResourceContextImpl c = (ResourceContextImpl) i.next();
        if (c.getResourceName().equals(resource.getName())) {
          c.resource = resource;
          i.remove();
          activateContext(c);
        }
      }
    } else {
      ArrayList<ConsumerTypeEntry> newTypes = new ArrayList<ConsumerTypeEntry>(consumerTypes.length);
      for (ConsumerTypeEntry typeEntry :consumerTypes)  {
       for(int i = 0; i < typeEntry.contexts.length; i++) {
         if (typeEntry.contexts[i].getResourceName().equals(resource.getName())) {
           typeEntry.contexts[i].setActive(false);
           inactiveContexts.add(typeEntry.contexts[i]);
           if (typeEntry.contexts.length != 1) {
             ResourceContextImpl[] temp = new ResourceContextImpl[typeEntry.contexts.length - 1];
             System.arraycopy(typeEntry.contexts, 0, temp, 0, i);
             System.arraycopy(typeEntry.contexts, i + 1, temp, 0, typeEntry.contexts.length - i);
             typeEntry.contexts = temp;
             newTypes.add(typeEntry);
           }
           break;
         }
       }
     }
     consumerTypes = newTypes.toArray(new ConsumerTypeEntry[newTypes.size()]);
   }
  }

  private void activateContext(ResourceContextImpl context) {
      context.setActive(true);
      for (ConsumerTypeEntry typeEntry :consumerTypes){
        if (typeEntry.type.equals(context.getConsumerType())) {
          ResourceContextImpl[] temp = new ResourceContextImpl[typeEntry.contexts.length +1];
          System.arraycopy(typeEntry.contexts, 0, temp, 0, typeEntry.contexts.length);
          temp[typeEntry.contexts.length] = context;
          typeEntry.contexts = temp;
          return;
        }
      }
      ConsumerTypeEntry[] temp = new ConsumerTypeEntry[consumerTypes.length + 1];
      System.arraycopy(consumerTypes, 0, temp, 0, consumerTypes.length);
      ConsumerTypeEntry newTypeEntry = new ConsumerTypeEntry();
      newTypeEntry.type = context.getConsumerType();
      newTypeEntry.contexts = new ResourceContextImpl[1];
      newTypeEntry.contexts[0] = context;
      temp[consumerTypes.length] = newTypeEntry;
      consumerTypes = temp;
  }

  public long getUsage(ResourceConsumer consumer, String resourceName) {
    for(ConsumerTypeEntry consumerType:consumerTypes) {
      if (consumerType.type.equals(consumer.getType())) {
        for(ResourceContextImpl resourceContext:consumerType.contexts) {
          if (resourceContext.getResourceName().equals(resourceName)) {
              WeakReference weak = resourceContext.mapTable.get(consumer.getId());
              if (weak != null) {
                ConsumerCounter counter = (ConsumerCounter)weak.get();
                if (counter != null) {
                  return counter.longValue();
                }
              }
           }
         }
      }
    }
    return 0;
  }

  public long getTotalUsage(String consumerType, String resourceName) {
    for(ConsumerTypeEntry entry:consumerTypes) {
      if (entry.type.equals(consumerType)) {
        for(ResourceContextImpl resourceContext:entry.contexts) {
          if (resourceContext.getResourceName().equals(resourceName)) {
             return resourceContext.counter.intValue();
          }
        }
      }
    }
    return 0;
  }

  public synchronized Resource[] getRegisteredResources() {
    Resource[] res = new Resource[resources.size()];
    int i = 0;
    for (ResourceProvider provider: resources.values()) {
      res[i] = provider.getResource();
      i++;
    }
    return res;
  }

  public String[] getActiveConsumerTypes() {
    HashSet<String> set = new HashSet<String>();
    for (ConsumerTypeEntry entry:consumerTypes) {
      set.add(entry.type);
    }
    return set.toArray(new String[set.size()]);
  }


}
