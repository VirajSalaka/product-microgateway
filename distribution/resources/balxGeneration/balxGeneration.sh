#!/bin/bash

# OS specific support.  $var _must_ be set to either true or false.
cygwin=false
os400=false
case "`uname`" in
CYGWIN*) cygwin=true;;
OS400*) os400=true;;
esac

# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '.*/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

# Get standard environment variables
PRGDIR=`dirname "$PRG"`

# if MICROGW_HOME environment variable not set then set it relative to the micro-gw binary location
if [[ -z "${MICROGW_HOME}" ]]; then
  export MICROGW_HOME=`cd "$PRGDIR/.." ; pwd`
fi

# set BALLERINA_HOME
BALLERINA_HOME="$MICROGW_HOME/lib/platform"

export BALLERINA_HOME=$BALLERINA_HOME
export PATH=$BALLERINA_HOME/bin:$PATH

# needs to handle the scenario where no arg is provided
IS_BUILD_COMMAND=true
CMD_PRO_NAME_VAL="$1"
MICRO_GW_PROJECT_DIR="$2"

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin; then
  [ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
  [ -n "$BALLERINA_HOME" ] && BALLERINA_HOME=`cygpath --unix "$BALLERINA_HOME"`
  [ -n "$MICROGW_HOME" ] && MICROGW_HOME=`cygpath --unix "$MICROGW_HOME"`
fi

# For OS400
if $os400; then
  # Set job priority to standard for interactive (interactive - 6) by using
  # the interactive priority - 6, the helper threads that respond to requests
  # will be running at the same priority as interactive jobs.
  COMMAND='chgjob job('$JOBNAME') runpty(6)'
  system $COMMAND

  # Enable multi threading
  QIBM_MULTI_THREADED=Y
  export QIBM_MULTI_THREADED
fi

# For Migwn, ensure paths are in UNIX format before anything is touched
if $mingw ; then
  [ -n "$BALLERINA_HOME" ] &&
    BALLERINA_HOME="`(cd "$BALLERINA_HOME"; pwd)`"
  [ -n "$JAVA_HOME" ] &&
    JAVA_HOME="`(cd "$JAVA_HOME"; pwd)`"
  [ -n "$MICROGW_HOME" ] &&
    MICROGW_HOME="`(cd "$MICROGW_HOME"; pwd)`"
fi

if [ -z "$JAVACMD" ] ; then
  if [ -n "$JAVA_HOME"  ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
      # IBM's JDK on AIX uses strange locations for the executables
      JAVACMD="$JAVA_HOME/jre/sh/java"
    else
      JAVACMD="$JAVA_HOME/bin/java"
    fi
  else
    JAVACMD=java
  fi
fi

if [ ! -x "$JAVACMD" ] ; then
  echo "Error: JAVA_HOME is not defined correctly."
  exit 1
fi

# if JAVA_HOME is not set we're not happy
if [ -z "$JAVA_HOME" ]; then
  echo "You must set the JAVA_HOME variable before running Ballerina."
  exit 1
fi



if [ "$IS_BUILD_COMMAND" = true ] && [ "$CMD_PRO_NAME_VAL" != "" ] && [ "$MICRO_GW_PROJECT_DIR" != "" ]; then
    MICRO_GW_LABEL_PROJECT_DIR="$MICRO_GW_PROJECT_DIR/$CMD_PRO_NAME_VAL"
    echo $MICRO_GW_LABEL_PROJECT_DIR
    pushd $MICRO_GW_LABEL_PROJECT_DIR > /dev/null
        # clean the content of target folder
        if [ -d "$MICRO_GW_LABEL_PROJECT_DIR/target" ]; then
            rm -rf $MICRO_GW_LABEL_PROJECT_DIR/target
        fi
        # build the ballerina source code for the label
        ballerina build src/ -o $CMD_PRO_NAME_VAL.balx --offline
    popd > /dev/null
fi