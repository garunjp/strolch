#!/bin/bash

if [ ! $(which xmlstarlet) ] ; then
  echo "ERROR: xmlstarlet is missing!"s
  exit 1
fi

function usage() {
  echo "Usage: $0 [-c] [-p] [-r <release_branch>] [-b <branch>] [-o <old_version>] [-n <new_version>]" 1>&2;
  exit 1;
}

while getopts ":b:o:n:r:cp" arg; do
    case "${arg}" in
        c)
            c="true"
            ;;
        p)
            p="true"
            ;;
        r)
            r=${OPTARG}
            ;;
        b)
            b=${OPTARG}
            ;;
        o)
            o=${OPTARG}
            ;;
        n)
            n=${OPTARG}
            ;;
        *)
            echo "ERROR: Unknown arg ${arg}"
            usage
            ;;
    esac
done
shift $((OPTIND-1))

if [ -z "${r}" ] || [ -z "${b}" ] || [ -z "${o}" ] || [ -z "${n}" ] ; then
  echo "ERROR: Missing an argument!"
  usage
fi

if [ "$(git status --short)" != "" ] ; then
  echo "You have uncommitted changes!"
  exit 1
fi


declare SCRIPT_NAME="${0##*/}"
declare SCRIPT_DIR="$(cd ${0%/*} ; pwd)"

create_release_branch="${c}"
release_branch="${r}"
branch="${b}"
old_version="${o}"
new_version="${n}"
push="${p}"

root="$(cd ${SCRIPT_DIR}/.. ; pwd)"


echo "root=${root}"
echo "old_version=${old_version}"
echo "new_version=${new_version}"
echo "branch=${branch}"
echo "release_branch=${release_branch}"
if [ -n "${create_release_branch}" ] ; then
  echo "Creating release branch."
else
  echo "NOT creating release branch."
fi
if [ -n "${push}" ] ; then
  echo "Pushing to origin."
else
  echo "NOT pusing to origin."
fi

echo ""

function fail() {
  echo -e "\nWARN: Cleaning up after fail..."
  git submodule foreach git reset --hard origin/${branch}
  git submodule foreach git checkout ${branch}
  if [ -n "${create_release_branch}" ] ; then
    git submodule foreach git branch -D ${release_branch}
  fi
  git submodule foreach git tag -d ${new_version}

  git checkout ${branch}
  git reset --hard origin/${branch}
  if [ -n "${create_release_branch}" ] ; then
    git branch -D ${release_branch}
  fi
  git tag -d ${new_version}

  echo -e "\nERROR: Failed to release version ${new_version}"
  exit 1
}


# create release branch
if [ -n "${create_release_branch}" ] ; then
  echo -e "\nINFO: Creating release branch..."
  if ! git branch ${release_branch} ${branch} ; then
    fail
  fi
  if ! git submodule foreach git branch ${release_branch} ${branch} ; then
    fail
  fi
fi


# checkout release branch
echo -e "\nINFO: Checking out release branch..."
if ! git checkout ${release_branch} ; then
  fail
fi
echo -e "\nINFO: Checking out submodules..."
if ! git submodule foreach git checkout ${release_branch} ; then
  fail
fi


# bump version
echo -e "\nINFO: Bumping versions..."
${root}/li.strolch.dev/setVersion.sh ${old_version} ${new_version}
if [ $? -ne 0 ] ; then
  fail
fi


# show status
echo -e "\nINFO: Git Status:"
if ! git status --short ; then
  fail
fi
if ! git submodule foreach git status --short ; then
  fail
fi


# commit and tag submodules
echo -e "\nINFO: Committing and tagging submodules..."
if ! git submodule foreach git add . ; then
  fail
fi
if ! git submodule foreach git commit -m "[Project] bumped version from ${old_version} to ${new_version}" ; then
  fail
fi
if ! git submodule foreach git tag ${new_version} ; then
  fail
fi


# commit and tag including ref to submodules
echo -e "\nINFO: Synching submodules..."
if ! git submodule sync ; then
  fail
fi
echo -e "\nINFO: Committing and tagging..."
if ! git add . ; then
  fail
fi
if ! git commit -m "[Project] bumped version from ${old_version} to ${new_version}" ; then
  fail
fi
if ! git tag ${new_version} ; then
  fail
fi


# update local working directory to original branch
echo -e "\nINFO: Updating local work directory..."
if ! git submodule foreach git checkout ${branch} ; then
  fail
fi
if [ -n "${create_release_branch}" ] ; then
  echo -e "\nINFO: Removing submodule release branches..."
  if ! git submodule foreach git branch -D ${release_branch} ; then
    fail
  fi
fi
echo -e "\nINFO: Checking out branch..."
if ! git checkout ${branch} ; then
  fail
fi
echo -e "\nINFO: Removing release branch..."
if [ -n "${create_release_branch}" ] ; then
  if ! git branch -D ${release_branch} ; then
    fail
  fi
fi
echo -e "\nINFO: Updating submodules..."
if ! git submodule update ; then
  fail
fi


# push to origin
if [ -n "${push}" ] ; then
  echo -e "\nINFO: Pushing to origin..."
  git push origin ${new_version}
  git submodule foreach git push origin ${new_version}
fi


echo -e "\nINFO: Released version ${new_version}"

exit 0
