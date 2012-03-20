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
* Gracefully handle modification of default parameters
* Release to central
* Support storages other than filesystem
* Integrate https://github.com/typesafehub/config
* Support substitution
* Support storage formats other than yaml

Sample
------
https://github.com/OlegYch/config-beans/blob/master/src/test/scala/com/olegych/config/beans/yaml/ConfigSample.scala