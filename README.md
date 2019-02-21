# propertea

[![Build Status][travis-badge]][travis]
[![Dependencies Status][deps-badge]][deps]
[![Clojars Project][clojars-badge]][clojars]
[![Tag][tag-badge]][tag]
[![Clojure version][clojure-v]](project.clj)

[![][logo]][logo-large]

*Simple property loading, coercing, and validating*


#### Contents

* [Usage](#usage-)
  * [Reading Property Files and Streams](#reading-property-files-and-streams-)
  * [Maps <-> Properties](#maps---properties-)
* [Installing](#installing-)
* [Version History](#version-history-)
* [License](#license-)


## Usage [&#x219F;](#contents)

### Reading Property Files and Streams [&#x219F;](#contents)

propertea can be used to load a property file, convert, and validate. The
following snippet shows loading a file and converting a few of the properties
to their desired types.

Given the properties file `test/fake.properties` with the following contents:

```
string-example=hello-string
int-example=1
boolean-example=true
empty-string=
nested.example.depth=5
nested.example.leaves=2
nested.withCamelCase=get-dashed
```

```clj
(require '[propertea.core :as prop])

(prop/read "test/fake.properties"
           :as-int [:int-example]
           :as-bool [:boolean-example])
```
```clj
{:int-example 1, :string-example "hello-string", :boolean-example true}
```

An input stream can be used instead of a filename. This is useful to read
property files in a Jar:

```clj
(prop/read
  (.getResourceAsStream
    (.getContextClassLoader (Thread/currentThread))
    "some.properties"))
```

or

```clj
(require '[clojure.java.io :as io])

(-> (io/resource "some.properties")
    (io/file)
    (io/input-stream)
    (prop/read))
```

propertea can also validate that required properties are specified. Assuming
the same properties file as above:

```clj
(prop/read "test/fake.properties" :required [:req-prop]))
```
```clj
java.lang.RuntimeException: (:req-prop) are required, but not found
```


## Maps <-> Properties [&#x219F;](#contents)

Some Java libraries expect data (often configuration data) to be in the form of
`java.util.Properties`. propertea makes this a sane prospect when interoping
with such libraries:

```clj
(def m2p (prop/map->props {:thing1 "one" :thing2 "two"}))
m2p
```
```clj
{"thing2" "two", "thing1" "one"}
```
```clj
(type m2p)
```
```clj
java.util.Properties
```

You can, of course, take this in the other direction:

```clj
(prop/props->map m2p)
```
```clj
{"thing2" "two", "thing1" "one"}
```

As you can see, by default that returns hash-maps with keys as strings. If you
pass a function in addition to the data, that function will be applied to each
keyword:

```clj
(prop/props->map m2p keyword)
```
```clj
{:thing2 "two", :thing1 "one"}
```


## Installing [&#x219F;](#contents)

The easiest way is via Leiningen. Add the following dependency to your
`project.clj` file:

```clj
[clojusc/propertea "1.5.0-SNAPSHOT"]
```

To build from source and install locally, run the following commands:

```
$ lein deps
$ lein jar
$ lein install
```


## Version History [&#x219F;](#contents)

| Release     | Clojure     | Maintainer     | Notes
| ----------- | ----------- | -------------- | ---------------------------------- |
| 1.5.0       | 1.10.0      | clojusc        | Backwards-compatible function changes; breaking arg changes |
| 1.4.2       | 1.10.0      | clojusc        | 100% compatible with 1.4.1         |
| 1.4.1       | 1.5.1       | Joshua Eckroth | Version from which clojusc forked  |
| 1.3.1       | 1.2.0       | Jay Fields     | Version from which Josh forked     |


## License [&#x219F;](#contents)

BSD 3-Clause License

Copyright © 2010-2012, Jay Fields

Copyright © 2013-2014, Joshua Eckroth

Copyright © 2019, Clojure-Aided Enrichment Center

All rights reserved.


<!-- Named page links below: /-->

[travis]: https://travis-ci.org/clojusc/propertea
[travis-badge]: https://travis-ci.org/clojusc/propertea.png?branch=master
[deps]: http://jarkeeper.com/clojusc/propertea
[deps-badge]: http://jarkeeper.com/clojusc/propertea/status.svg
[logo]: resources/images/nextstep-properties.gif
[logo-large]: resources/images/nextstep-properties.gif
[tag-badge]: https://img.shields.io/github/tag/clojusc/propertea.svg
[tag]: https://github.com/clojusc/propertea/tags
[clojure-v]: https://img.shields.io/badge/clojure-1.10.0-blue.svg
[clojars]: https://clojars.org/clojusc/propertea
[clojars-badge]: https://img.shields.io/clojars/v/clojusc/propertea.svg
