/*
 * Copyright (C) 2010 Israfil, Inc.
 * Copyright (C) 2013 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.israfil.gcommander;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterDescription;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.name.Names;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.israfil.gcommander.FluentAPI.WithArguments;

public class JCommanderModuleBuilder implements
    FluentAPI.WithArguments, FluentAPI.WithPrefix, FluentAPI.Build {

  private final List<Class<?>> parameterObjectTypes;
  private String[] arguments;
  private String prefix = "";

  private JCommanderModuleBuilder(List<Class<?>> types) {
    this.parameterObjectTypes = types;
  }

  public static FluentAPI.WithPrefix bindParameters(Class<?> ... classes) {
    return new JCommanderModuleBuilder(Arrays.asList(classes));
  }

  @Override
  public FluentAPI.Build withArguments(String ... argv) {
    this.arguments = argv;
    return this;
  }

  @Override
  public WithArguments withPrefix(String prefix) {
    this.prefix = prefix;
    return this;
  }

  @Override
  public Module build() {
    return new Module() {
      @Override
      public void configure(Binder binder) {
        Map<Class<?>, Object> instances = new HashMap<Class<?>, Object>();
        for (Class<?> type : parameterObjectTypes) {
          try {
            instances.put(type, type.newInstance());
          } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate " + type.getSimpleName());
          }
        }
        // populate instances
        JCommander cmd = new JCommander(instances.values());
        cmd.parse(arguments);

        // bind parameter object instances
        for (Map.Entry<Class<?>, Object> entry : instances.entrySet()) {
          binder.bind((Class)entry.getKey()).toInstance(entry.getValue());
        }

        // bind parameters as named objects.
        for(ParameterDescription pdesc : cmd.getParameters()) {
          Object parameterObject = pdesc.getObject();
          for (String name : pdesc.getParameter().names()) {
            Object value = pdesc.getParameterized().get(parameterObject);
            Class<?> type = pdesc.getParameterized().getType();
            if (Iterable.class.isAssignableFrom(type) && value == null) {
                value = createEmptyCollection(type); // if null, make an empty collection (or whatever)
            }
            bindToType(binder, type, name, value);
          }
        }
      }

      void bindToType(Binder b, Class<?> type, String name, Object value) {
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
          abb.annotatedWith(Names.named(prefix.concat(processName(name))));
        }
        abb.toInstance(value);
      }

      private Object createEmptyCollection(Class<?> c) {
        if (c == null) return null;
        if (Set.class.isAssignableFrom(c)) {
          return new HashSet();
        } else {
          return new ArrayList();
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
