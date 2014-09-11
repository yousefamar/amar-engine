#!/bin/sh

# Uncomment OS if not running jar.

# Linux
java -Djava.library.path="lib/native/linux" -cp "bin:lib/*" com.metaplains.core.GameClient

# Mac OSX
#java -Djava.library.path="lib/native/macosx" -cp "bin:lib/*" com.metaplains.core.GameClient

# Solaris
#java -Djava.library.path="lib/native/solaris" -cp "bin:lib/*" com.metaplains.core.GameClient
