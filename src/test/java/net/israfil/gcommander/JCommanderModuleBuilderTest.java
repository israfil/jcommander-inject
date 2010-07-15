package net.israfil.gcommander;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

public class JCommanderModuleBuilderTest {
  @Test
  public void testArgs() {
    String args = "-groups foo --debug a b c";
    String[] argv = args.split("[ ]+");
    Injector i = Guice.createInjector(
      new JCommanderModuleBuilder().bindParameters(AppParameters.class)
          .withArguments(argv).build(),
      new Module() {
        public void configure(Binder binder) {
          binder.bind(MyClass.class);
          binder.bind(new TypeLiteral<Set<Long>>(){}).toInstance(new HashSet<Long>());
        }
      }
    );
    MyClass myClass = i.getInstance(MyClass.class);
    assertNotNull(myClass);
    assertTrue(myClass.debug);
    assertNotNull(myClass.other);
    assertEquals("foo", myClass.other.groups);
  }    
}
