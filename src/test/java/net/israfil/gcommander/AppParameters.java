package net.israfil.gcommander;

import java.util.List;
import java.util.Set;

import com.beust.jcommander.Lists;
import com.beust.jcommander.Parameter;

public class AppParameters {
  @Parameter
  public List<String> parameters = Lists.newArrayList();

  @Parameter(names = { "-log", "-verbose" }, description = "Level of verbosity")
  public Integer verbose = 1;

  @Parameter(names = "-groups", description = "Comma-separated list of group names to be run")
  public String groups;

  @Parameter(names = "--debug", description = "Debug mode")
  public boolean debug = false;
  
  @Parameter(names = "--blah", description = "How bored is this app.")
  public Set<Boolean> boredom;
  
}