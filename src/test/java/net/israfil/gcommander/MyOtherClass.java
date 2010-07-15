package net.israfil.gcommander;

import java.util.Set;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class MyOtherClass {
  public String groups;
  public Set<Boolean> boredom;
  @Inject
  public MyOtherClass(
      @Named("groups") String groups,
      @Named("blah") Set boredom) {
    this.groups = groups;
    this.boredom = boredom;
  }
}