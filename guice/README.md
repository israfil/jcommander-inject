JCommander-Guice
==========

[JCommander](http://github.com/cbeust/jcommander) is a command-line parameter parsing
framework created by C&eacute;dric Beust.  It lets you create annotated parameter objects,
and handles parsing, type coercion, and various kinds of error reporting for the library user.

JCommander-Guice is an integration between JCommander and Guice - a sort of flag-binder.  
JCommander-Guice acts as a Module builder, allowing you to simply pass in the JCommander-style
annotated parameter objects, an optional prefix, and the args String[].  JCommanderModuleBuilder
then invokes the JCommander candy goodness, and creates a Module which binds each parameter
to @Named-qualified, strongly typed values in the injector.  This permits you to simply inject
the parameter values where you need them. 

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
    class MyClass {
      final Boolean debug;
      final Integer debugVerbosity;
      final MyOtherClass other;
      @Inject MyClass(
        @Named("debug") Boolean debug,
        @Named("verbose") Integer debugVerbosity,
        MyOtherClass other
      ) {
        this.debug = debug;
        this.debugVerbosity = debugVerbosity;
        this.other = other;
      }
    }

    // Or field injection, whatevz...
    class MyOtherClass {
      @Inject @Named("groups") String groups;
    }
    
    class MyModule implements Module {
      public void configure(Binder binder) {
        binder.bind(MyClass.class);
        binder.bind(MyOtherClass.class);
      }
    }

And bind it like this

    Assume main() args: "-groups foo -verbose -verbose -debug a b c";
    Injector i = Guice.createInjector(
        JCommanderModuleBuilder.bindParameters(AppParameters.class).withArguments(args).build(),
        new MyModule());
    MyClass myClass = i.getInstance(MyClass.class);
    
    // Testing this should result in
    assertNotNull(myClass);
    assertTrue(myClass.debug);
    assertNotNull(myClass.other);
    assertEquals("foo", myClass.other.groups);
      
If you want, you can add a prefix:

    // created just for an injection example.
    public class MyClass {
      @Inject @Named("cli.debug") Boolean debug;
      @Inject @Named("cli.verbose") Integer debugVerbosity;
      @Inject MyOtherClass other;
    }

    public class MyOtherClass {
      @Inject @Named("cli.groups") String groups;
    }

And bind it like so: 

    // Assume: "-groups foo -verbose -verbose -debug a b c"
    public static main(String[] args) {
    Injector i = Guice.createInjector(
      JCommanderModuleBuilder.bindParameters(AppParameters.class)
          .withPrefix("cli.").withArguments(args).build(),
    );
    
You can also directly inject the parameter objects. 

    Assume main() args: "-groups foo -verbose -verbose -debug a b c";
    Injector i = Guice.createInjector(
        JCommanderModuleBuilder.bindParameters(AppParameters.class).withArguments(args).build(),
        new MyModule());
    // Fetching from injector - better to inject directly.
    AppParameters myClass = i.getInstance(AppParameters.class);
    
    // Testing this should result in
    assertNotNull(myClass);
