USE vdicore;

TRUNCATE desktop;

TRUNCATE desktoppool;

TRUNCATE desktoppoolstatus;

TRUNCATE desktopstatus;

TRUNCATE asyncjob;

TRUNCATE organization;

TRUNCATE groups;

DELETE FROM domain WHERE iddomain <> 0;

DELETE FROM user WHERE iduser <> 0;

TRUNCATE groupelement;

DELETE FROM administrator WHERE userid <> 0;

TRUNCATE cloudmanager;

TRUNCATE cloudmanagerstatus;

TRUNCATE session;

TRUNCATE connection;

TRUNCATE resourceassignment;

UPDATE runtimevariable SET value = 'n';

INSERT INTO organization (idorganization, organizationtype, domainid, organizationname, level, parent, notes) VALUES
                         (0,              0,                0,        'default',        0,     -1,     'notes');
update organization SET idorganization = 0;

INSERT INTO groups (idgroup, groupname, notes) VALUES
                   (1,       'group1',  'notes');
INSERT INTO groups (idgroup, groupname, notes) VALUES
                   (2,       'group2',  'notes');

INSERT INTO user (iduser, usertype, domainid, organizationid, username, password,                           realname, idcardtype, idcard,               email,          address,   telephone,     notes) VALUES
                 (1,      0,        0,        0,              'u1',     '96e79218965eb72c92a549dd5a330112', 'Usr1',   'idcard',   '111111111111111111', 'jack@opz.com', 'address', '13999999999', 'notes');
INSERT INTO user (iduser, usertype, domainid, organizationid, username, password,                           realname, idcardtype, idcard,               email,          address,   telephone,     notes) VALUES
                 (2,      0,        0,        0,              'u2',     '96e79218965eb72c92a549dd5a330112', 'Usr2',   'idcard',   '111111111111111111', 'jack@opz.com', 'address', '13999999999', 'notes');
INSERT INTO user (iduser, usertype, domainid, organizationid, username, password,                           realname,  idcardtype, idcard,               email,          address,   telephone,     notes) VALUES
                 (3,      0,        0,        0,              'o0m',    '96e79218965eb72c92a549dd5a330112', 'Org0Mgr', 'idcard',   '111111111111111111', 'jack@opz.com', 'address', '13999999999', 'notes');
INSERT INTO user (iduser, usertype, domainid, organizationid, username, password,                           realname,  idcardtype, idcard,               email,          address,   telephone,     notes) VALUES
                 (4,      0,        0,        0,              'g1m',    '96e79218965eb72c92a549dd5a330112', 'Grp1Mgr', 'idcard',   '111111111111111111', 'jack@opz.com', 'address', '13999999999', 'notes');
INSERT INTO user (iduser, usertype, domainid, organizationid, username, password,                           realname,  idcardtype, idcard,               email,          address,   telephone,     notes) VALUES
                 (5,      0,        0,        0,              'g2m',    '96e79218965eb72c92a549dd5a330112', 'Grp2Mgr', 'idcard',   '111111111111111111', 'jack@opz.com', 'address', '13999999999', 'notes');
INSERT INTO user (iduser, usertype, domainid, organizationid, username, password,                           realname,      idcardtype, idcard,               email,          address,   telephone,     notes) VALUES
                 (6,      0,        0,        0,              'g1g2m',  '96e79218965eb72c92a549dd5a330112', 'Grp1Grp2Mgr', 'idcard',   '111111111111111111', 'jack@opz.com', 'address', '13999999999', 'notes');

INSERT INTO groupelement (idgroupelement, groupid, elementid, elementtype) VALUES
                         (1,              1,       1,         0x11);
INSERT INTO groupelement (idgroupelement, groupid, elementid, elementtype) VALUES
                         (2,              1,       2,         0x11);
INSERT INTO groupelement (idgroupelement, groupid, elementid, elementtype) VALUES
                         (3,              2,       2,         0x11);

INSERT INTO administrator (idadministrator, userid, targettype, targetid) VALUES
                          (2,               3,      18,         0);
INSERT INTO administrator (idadministrator, userid, targettype, targetid) VALUES
                          (3,               4,      32,         1);
INSERT INTO administrator (idadministrator, userid, targettype, targetid) VALUES
                          (4,               5,      32,         2);
INSERT INTO administrator (idadministrator, userid, targettype, targetid) VALUES
                          (5,               6,      32,         1);
INSERT INTO administrator (idadministrator, userid, targettype, targetid) VALUES
                          (6,               6,      32,         2);

INSERT INTO cloudmanager (idcloudmanager, cloudname, baseurl,               username,     password,      domain, clouddrivername, notes) VALUES
                         (2,              'opzc',    'http://192.188.46.5', '/root/root', 'opzooncloud', '',     'OpzoonCloud',   'notes');
INSERT INTO cloudmanager (idcloudmanager, cloudname, baseurl,                                username, password, domain, clouddrivername, notes) VALUES
                         (1,              'cs',      'http://192.168.31.67:8080/client/api', 'admin',  'opzoon', '',     'Cloudstack',    'notes');

INSERT INTO cloudmanagerstatus (idcloudmanager, status) VALUES
                               (2,              0);
INSERT INTO cloudmanagerstatus (idcloudmanager, status) VALUES
                               (1,              0);

INSERT INTO desktoppool (iddesktoppool, poolname,     vmsource, assignment, cloudmanagerid, templateid, templatename, vmnamepatterrn, computernamepattern, maxdesktops, sparedesktops, notes) VALUES
                        (1,             'first pool', 0,        0,          1,              'tpl',      'tpl',        'dev',          'pc',                2,           1,             'notes');
INSERT INTO desktoppool (iddesktoppool, poolname,      vmsource, assignment, cloudmanagerid, templateid, templatename, vmnamepatterrn, computernamepattern, maxdesktops, sparedesktops, notes) VALUES
                        (2,             'second pool', 1,        1,          1,              NULL,       NULL,         'dev',          'pc',                1,           1,             'notes');

INSERT INTO desktoppoolstatus (iddesktoppool, status, abnormaldesktops, sparingdesktops) VALUES
                              (1,             0,      0,                1);
INSERT INTO desktoppoolstatus (iddesktoppool, status, abnormaldesktops, sparingdesktops) VALUES
                              (2,             0,      0,                1);

INSERT INTO desktop (iddesktop, desktoppoolid, vmid,                                              vmname, ownerid) VALUES
                    (1,         1,             '/root/root/e3784950-d8f7-4e36-953d-56cde08ee62e', 'vm1',  1);
INSERT INTO desktop (iddesktop, desktoppoolid, vmid,                                              vmname, ownerid) VALUES
                    (2,         1,             '/root/root/e3784950-d8f7-4e36-953d-56cde08ee621', 'vm2',  2);
INSERT INTO desktop (iddesktop, desktoppoolid, vmid,                                              vmname, ownerid) VALUES
                    (3,         2,             '/root/root/e3784950-d8f7-4e36-953d-56cde08ee62e', 'vm1',  -1);

INSERT INTO desktopstatus (iddesktop, status, ownerid) VALUES
                          (1,         2,      1);
INSERT INTO desktopstatus (iddesktop, status, ownerid) VALUES
                          (2,         0,      2);
INSERT INTO desktopstatus (iddesktop, status, ownerid) VALUES
                          (3,         2,      -1);
                    
INSERT INTO asyncjob (jobid, cmd,          createtime,   jobstatus, jobprocstatus, handle,  jobresultcode, jobresult) VALUES
                     (1,     'createUser', '2011-01-01', 1,         100,           'DUMMY', 0,             '');

INSERT INTO resourceassignment (idresourceassignment, resourcetype, resourceid, visitortype, visitorid, permission) VALUES
                               (1,                    0x001,        1,          0x11,        1,         1);