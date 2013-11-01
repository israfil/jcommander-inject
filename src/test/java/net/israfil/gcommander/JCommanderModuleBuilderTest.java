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

import com.beust.jcommander.Parameter;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.Test;

import static org.truth0.Truth.ASSERT;

public class JCommanderModuleBuilderTest {

  private static final String[] ARGV = "-groups foo --debug a b c".split("[ ]+");

  static class AppParameters {
    @Parameter
    public List<String> parameters = new ArrayList<String>();

    @Parameter(names = { "-log", "-verbose" }, description = "Level of verbosity")
    public Integer verbose = 1;

    @Parameter(names = "-groups", description = "Comma-separated list of group names to be run")
    public String groups;

    @Parameter(names = "--debug", description = "Debug mode")
    public boolean debug = false;

    @Parameter(names = "--blah", description = "How bored is this app.")
    public Set<Boolean> boredom;

  }

  static class MyClass {
    @Inject @Named("debug") public Boolean debug;
    @Inject @Named("verbose") public Integer debugVerbosity;
    @Inject public MyOtherClass other;
  }

  static class MyOtherClass {
    @Inject @Named("groups") public String groups;
    @Inject @Named("blah") public Set boredom;
  }

  @Test public void testBasicsArgs() {
    Injector i = Guice.createInjector(
        JCommanderModuleBuilder.bindParameters(AppParameters.class)
            .withArguments(ARGV).build());
    MyClass myClass = i.getInstance(MyClass.class);
    ASSERT.that(myClass).isNotNull();
    ASSERT.that(myClass.debug).isTrue();
    ASSERT.that(myClass.other).isNotNull();
    ASSERT.that(myClass.other.groups).is("foo");
  }

  static class MyPrefixedClass {
    @Inject @Named("cli.debug") public Boolean debug;
    @Inject @Named("cli.verbose") public Integer debugVerbosity;
    @Inject public MyOtherPrefixedClass other;
  }

  static class MyOtherPrefixedClass {
    @Inject @Named("cli.groups") public String groups;
    @Inject @Named("cli.blah") public Set boredom;
  }

  @Test public void testPrefixedArgs() {
    Injector i = Guice.createInjector(
        JCommanderModuleBuilder.bindParameters(AppParameters.class)
            .withPrefix("cli.")
            .withArguments(ARGV).build());
    MyPrefixedClass myClass = i.getInstance(MyPrefixedClass.class);
    ASSERT.that(myClass).isNotNull();
    ASSERT.that(myClass.debug).isTrue();
    ASSERT.that(myClass.other).isNotNull();
    ASSERT.that(myClass.other.groups).is("foo");
  }

  @Test public void testParamterObject() {
    Injector i = Guice.createInjector(
        JCommanderModuleBuilder.bindParameters(AppParameters.class)
            .withPrefix("cli.")
            .withArguments(ARGV).build());
    AppParameters params = i.getInstance(AppParameters.class);
    ASSERT.that(params).isNotNull();
    ASSERT.that(params.parameters).has().allOf("a", "b", "c").inOrder();
    ASSERT.that(params.debug).isTrue();
  }

}
