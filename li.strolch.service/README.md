li.strolch.service
==================

[![Build Status](http://jenkins.eitchnet.ch/buildStatus/icon?job=li.strolch.service)](http://jenkins.eitchnet.ch/view/strolch/job/li.strolch.service/)

Service API for Strolch

Running tests
==================
* Install PostgreSQL and create the following users:
create user cacheduser with password 'test';
create database cacheduserdb;
GRANT ALL PRIVILEGES ON DATABASE cacheduserdb to cacheduser;
GRANT CONNECT ON DATABASE cacheduserdb TO cacheduser;

create user transactionaluser with password 'test';
create database transactionaluserdb;
GRANT ALL PRIVILEGES ON DATABASE transactionaluserdb to transactionaluser;
GRANT CONNECT ON DATABASE transactionaluserdb TO transactionaluser;

# You can revoke the privileges with the following:
revoke ALL PRIVILEGES ON DATABASE cacheduserdb from cacheduser;
drop user cacheduser;
drop database cacheduserdb;

revoke ALL PRIVILEGES ON DATABASE transactionaluserdb from transactionaluser;
drop user transactionaluser;
drop database transactionaluserdb;
