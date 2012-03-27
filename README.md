Easy and robust config files for Scala.
=========================================

Goals
---------
* Typesafe
* Maintainable
* Fast

Current Features
-----------
* No magic strings in your code
* yaml format for config files
* Supports Seq, Option, Symbol, primitives
* Code and config are kept in sync automatically (wip)

Plans
----------
* Support Map
* Support non-default constructors in case classes
* Release to central
* Gracefully handle modification of default parameters
* Support samples for collection values which are empty by default
* Support comments
* Support validation callbacks
* Support reload callbacks
* Support storages other than filesystem
* Integrate https://github.com/typesafehub/config
* Support substitution
* Support inclusion
* Support storage formats other than yaml

Sample
------
https://github.com/OlegYch/config-beans/blob/master/src/test/scala/com/olegych/config/beans/yaml/ConfigSample.scala