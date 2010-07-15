package net.israfil.gcommander;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Lists;
import com.beust.jcommander.ParameterDescription;
import com.beust.jcommander.Sets;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.name.Names;

public class JCommanderModuleBuilder implements 
    ModuleBuilderWithBoundParameters, ModuleBuilderWithArguments {
  
  private List<Class<?>> parameterObjectTypes;
  private String[] arguments;

  public ModuleBuilderWithBoundParameters bindParameters(Class<?> ... classes) {
    this.parameterObjectTypes = Arrays.asList(classes);
    return this;
  }

  public ModuleBuilderWithArguments withArguments(String ... argv) {
    this.arguments = argv;
    return this;
  }

  public Module build() {
    return new Module() {
      public void configure(Binder binder) {
        List<Object> instances = Lists.newArrayList();
        for (Class<?> type : parameterObjectTypes) {
          try {
            instances.add(type.newInstance());
          } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate " + type.getSimpleName());
          }
        }
        // populate instances
        JCommander cmd = new JCommander(instances);
        cmd.parse(arguments);
        
        // bind parameters as named objects.
        for(ParameterDescription pdesc : cmd.getParameters()) {
          for (String name : pdesc.getNames()) {
            Object value = pdesc.getValue();
            Class<?> type = pdesc.getField().getType();
            if (Iterable.class.isAssignableFrom(type) && value == null) {
                value = createEmptyCollection(type); // if null, make an empty collection (or whatever)
            }
            bindToType(binder, type, name, value);
          }
        }
      }
      
      public void bindToType(Binder b, Class<?> type, String name, Object value) {
        AnnotatedBindingBuilder abb = b.bind(
                (type == String.class) ?     String.class
              : (type == int.class) ?        Integer.class
              : (type == byte.class) ?       Byte.class
              : (type == long.class) ?       Long.class
              : (type == short.class) ?      Short.class
              : (type == float.class) ?      Float.class
              : (type == double.class) ?     Double.class
              : (type == char.class) ?       Character.class
              : (type == boolean.class) ?    Boolean.class
              : (type == Integer.class) ?    Integer.class
              : (type == Byte.class) ?       Byte.class
              : (type == Long.class) ?       Long.class
              : (type == Short.class) ?      Short.class
              : (type == Float.class) ?      Float.class
              : (type == Double.class) ?     Double.class
              : (type == Character.class) ?  Character.class
              : (type == Boolean.class) ?    Boolean.class
              : (type == Object.class) ?     Object.class
              : (type == Iterable.class) ?   Iterable.class
              : (type == Collection.class) ? Collection.class
              : (type == List.class) ?       List.class
              : (type == Set.class) ?        Set.class
              :                              null);
        if (abb == null) {
          throw new RuntimeException("Cannot bind parameter");
        }
        if (name != null) {
          abb.annotatedWith(Names.named(processName(name)));
        }
        abb.toInstance(value);
      }
      
      private Object createEmptyCollection(Class<?> c) {
        if (c == null) return null;
        if (Set.class.isAssignableFrom(c)) { 
          return Sets.newHashSet();
        } else {
          return Lists.newArrayList();
        }
      }

      private String processName(String name) {
        // TODO(cgruber) Add strategy for alternatives.
        while(name.length() > 1 && name.startsWith("-")) {
          name = name.substring(1);
        }
        return name;
      }
    };
  }
}
