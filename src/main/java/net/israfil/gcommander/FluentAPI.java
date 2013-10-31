package net.israfil.gcommander;

import com.google.inject.Module;

/**
 * Fluent API interfaces for JCommanderModuleBuilder.
 */
public final class FluentAPI {
  private FluentAPI() { }

  public interface Build {
    Module build();
  }

  public interface WithArguments {
    Build withArguments(String ... argv);
  }

  public interface WithPrefix extends WithArguments {
    WithArguments withPrefix(String prefix);
  }

}