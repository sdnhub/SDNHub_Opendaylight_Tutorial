#!/bin/bash

# Use same path for run.internal.sh
RUNSH_DIR=$(dirname $0)
CONTROLLER_RUNSH=${RUNSH_DIR}/run.internal.sh

OF_FILTER=
OF_PLUGIN_RUNTIME=
helparg=
# Added help for of13 before help from controller (this help common for intagration and controller)
function usage {
    needParentHelp=true
    if [ -n "${helparg}" ]; then
       . functions.sh
       harvestHelp ${helparg}
       if (( $? == 0 )); then
           needParentHelp=false
           echo -e '\nFor more information type -help.\n'
       fi
    else
        echo 'For more information on a specific command, type -help command-name.'
        echo
        echo '    Added option for integration:'
        echo '    of13             [-of13]'
        echo '    of10             [-of10]'
        echo
        echo '    Visit wiki for more information :'
        echo
        echo '    https://wiki.opendaylight.org/view/CrossProject:Integration_Group:Controller_Artifacts:run_sh'
        echo
        echo 'Common options: '
    fi

    if ${needParentHelp}; then
        $CONTROLLER_RUNSH -help ${helparg}
    fi
    exit 1
}

OF13=1
BUNDLEFILTER=
while true ; do
    (( i += 1 ))
    case "${@:$i:1}" in
        -of10) OF13=0 ; (( i += 1 ));;
        -of13) OF13=1 ; (( i += 1 ));;
        -bundlefilter) (( i += 1 )); BUNDLEFILTER="|${@:$i:1}";;
        -help) (( i += 1 )); helparg=${@:$i:1}; usage ;;
        "") break ;;
    esac
done

# clean available optional configurations (links)
find ${RUNSH_DIR}/configuration/initial -type l -exec rm {} \;

##of13
#of13             [-of13]
#   Option to run the OpenDaylight controller with the OpenFlow plugin (1.3).
##
# OF Filter selection
OF_FILTER="org.opendaylight.(openflowplugin|openflowjava|controller.sal-compatibility|ovsdb.of-extension)"
OF_PLUGIN_RUNTIME="1.0"
if (( $OF13 != 0 )); then
    OF_PLUGIN_RUNTIME="1.3"
    OF_FILTER="org.opendaylight.controller.(thirdparty.org.openflow|protocol_plugins.openflow)"
    while read ofConfig; do
        ln -s ../initial.available/$(basename ${ofConfig}) ${RUNSH_DIR}/configuration/initial/
    done < <(find ${RUNSH_DIR}/configuration/initial.available -name '*openflowplugin.xml')
fi

# Make sure we suck out our additional args so as to not confuse
# run.internal.sh
NEWARGS=`echo $@|sed 's/-of13//'|sed 's/-of10//'|sed 's/-bundlefilter[ ]*[^ ]*//'`

# Build the filter string
FILTERBEGINING='^(?!'
FILTERENDING=').*'
FILTER=${FILTERBEGINING}${OF_FILTER}${BUNDLEFILTER}${FILTERENDING}

# Run the command
$CONTROLLER_RUNSH -Dfelix.fileinstall.filter="$FILTER" -Dovsdb.of.version="$OF_PLUGIN_RUNTIME" $NEWARGS
