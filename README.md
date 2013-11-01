JCommander-Inject
==========

JCommander is a command-line parameter parsing framework by C�dric Beust.  It lets you
create annotated parameter objects, and handles parsing, type coercion, and various kinds of
error reporting for the library user.

JCommander-Inject provides integrations between JCommander and dependency injection frameworks
based on JSR-330 - a sort of flags-binder.  

Integrations:
  * [Guice](https://github.com/israfil/jcommander-inject/tree/master/guice) 
    (Released): Module builder approach
  * Dagger (Planned): Code-generation of a Dagger module
  * Others: No firm plans, contributions are welcome.
  