#!/bin/bash

projectName=strolch_bundle
projectVersion=0.1.0-SNAPSHOT
DIST_STROLCH="/var/www/eitch/www.strolch.li/dist/snapshot"
workDir="${PWD}/target/strolch_bundle"
projectsFile="${PWD}/projects.lst"

echo "INFO Bootstrapping..."
if ! ./bootstrap_https.sh ; then
  echo "ERROR: Failed to boostrap!"
  exit 1
fi

echo "INFO Pulling..."
if ! ./pull.sh ; then
  echo "ERROR: Failed to pull from upstream!"
  exit 1
fi

# first we create all needed packages
if ! mvn -DskipTests clean package ; then
  echo "ERROR: Failed to build packages!"
  exit 1
fi

# Make sure the work directory exists
if ! [ -d "${workDir}" ] ; then
  if ! mkdir -p "${workDir}" ; then
    echo "ERROR: Failed to create work dir ${workDir}."
    exit 1
  fi
fi

echo "INFO: Bundling projects..."

# Now copy those packages to our work dir
cd ..
while read project; do
  if [ "${project}" == "" ] ; then
  	continue;
  fi

  array=(${project//:/ })
  name="${array[0]}"
  tag="${array[1]}"

  if [ "${name}" == "" ] ||  [ "${tag}" == "" ] ; then
    echo -e "ERROR: Invalid project: ${project}! Must have form <git_name:tag>"
    exit 1
  fi

  if ! cd "${name}" ; then
    echo "ERROR: Could not switch to directory ${name}"
    exit 1
  fi

  echo "INFO: Copying ${name} packages..."
  if ls target/*.jar 2>/dev/null ; then
    if ! cp target/*.jar "${workDir}" ; then
      echo "ERROR: Failed to copy package for project ${name}."
      exit 1
    fi
    if ! cp target/*.jar "${DIST_STROLCH}" ; then
      echo "ERROR: Failed to publish package for project ${name}."
      exit 1
    fi
  elif ls target/*.war 2>/dev/null ; then
    if ! cp target/*.war "${workDir}" ; then
      echo "ERROR: Failed to copy wars for project ${name}."
      exit 1
    fi
    if ! cp target/*.war "${DIST_STROLCH}" ; then
      echo "ERROR: Failed to publish wars for project ${name}."
      exit 1
    fi
  else
    echo "INFO: Project ${name} has no target, skipping."
  fi

  cd ..

done < ${projectsFile}

cd ${workDir}/..
if ! tar -cvzf ${projectName}-${projectVersion}.tar.gz ${projectName} ; then
  echo "ERROR: Failed to make bundle."
  exit 1
fi

if ! mv ${projectName}-${projectVersion}.tar.gz "${DIST_STROLCH}" ; then
  echo "ERROR: Failed to publish bundle."
  exit 1
fi

echo "Done."
exit 0
