package net.israfil.gcommander;

import com.google.inject.Inject;
import com.google.inject.name.Named;

// created just for an injection example.
public class MyClass {
  public final Boolean      debug;
  public final Integer      debugVerbosity;
  public final MyOtherClass other;

  @Inject
  public MyClass(
      @Named("debug") Boolean debug, 
      @Named("verbose") Integer debugVerbosity,
      MyOtherClass other
  ) {
    this.debug = debug;
    this.debugVerbosity = debugVerbosity;
    this.other = other;
  }
}