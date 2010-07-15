GCommander
==========

This a Guice integration for JCommander

Here is a quick example:

    public class AppParameters {
      @Parameter
      public List<String> parameters = Lists.newArrayList();
  
      @Parameter(names = { "-log", "-verbose" }, description = "Level of verbosity")
      public Integer verbose = 1;
  
      @Parameter(names = "-groups", description = "Comma-separated list of group names to be run")
      public String groups;
  
      @Parameter(names = "-debug", description = "Debug mode")
      public boolean debug = false;
    }



and how you use it:

    // created just for an injection example.
    public class MyClass {
      public final Boolean debug;
      public final Integer debugVerbosity;
      public final MyOtherClass other;
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

    public class MyOtherClass {
      public String groups;
      public MyOtherClass(@Named("groups") String groups) {
        this.groups = groups;
      }
    }

    public void testArgs() {
      String args = "-groups foo -verbose -verbose -debug a b c";
      String[] argv = args.split("[ ]+");
      Injector i = Guice.createInjector(
        new JCommanderModuleBuilder().bindParameters(AppParameters.class)
            .withArguments(argv).build(),
        new Module() {
          public void configure(Binder binder) {
            binder.bind(MyClass.class);
          }
        }
      );
      MyClass myClass = i.getInstance(MyClass.class);
      assertNotNull(myClass);
      assertTrue(myClass.debug);
      assertNotNull(myClass.other);
      assertEquals("foo", myClass.other.groups);
    }    
      
