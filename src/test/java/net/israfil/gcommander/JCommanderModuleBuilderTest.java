package net.israfil.gcommander;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class JCommanderModuleBuilderTest {
  
  public static final Module TEST_MODULE = new Module() {
    public void configure(Binder binder) {
      binder.bind(MyClass.class);
    }
  };

  private static final String[] ARGV = "-groups foo --debug a b c".split("[ ]+");

  @Test
  public void testBasicsArgs() {
    Injector i = Guice.createInjector(
      new JCommanderModuleBuilder().bindParameters(AppParameters.class)
          .withArguments(ARGV).build(), TEST_MODULE
    );
    MyClass myClass = i.getInstance(MyClass.class);
    assertNotNull(myClass);
    assertTrue(myClass.debug);
    assertNotNull(myClass.other);
    assertEquals("foo", myClass.other.groups);
  }    

}
