DROP DATABASE IF EXISTS vdicore;

CREATE DATABASE vdicore CHARACTER SET UTF8;

USE vdicore;

CREATE TABLE domain
(
iddomain          INT       NOT NULL AUTO_INCREMENT COMMENT 'ID',
domaintype        INT       NOT NULL                COMMENT '0x0为本地域; 0x1为MSAD同步域; 0x2为ApacheDS同步域',
guid              CHAR(127) NOT NULL                COMMENT 'GUID',
domainname        CHAR(127) NOT NULL                COMMENT '域名称',
domainnetworkname CHAR(127) NOT NULL                COMMENT '域网络名称',
domainservername  CHAR(127) NOT NULL                COMMENT '域服务器地址',
domainserverport  INT       NOT NULL                COMMENT '域服务器端口',
domainbinddn      CHAR(127) NOT NULL                COMMENT '域服务器用户名',
domainbindpass    CHAR(127) NOT NULL                COMMENT '域服务器口令',
notes             CHAR(255) NOT NULL                COMMENT '备注',
status            INT       NOT NULL                COMMENT '状态. 0 正常; 1 维护中; 2 删除中; 3 同步中.',
ownerthread       INT       NULL                    COMMENT '当前正在操作的线程ID.',
CONSTRAINT domain_guid UNIQUE (guid),
PRIMARY KEY (iddomain)
) ENGINE InnoDB CHARACTER SET UTF8;



CREATE TABLE trace
(
idtrace          INT       NOT NULL  AUTO_INCREMENT COMMENT 'ID',
operatorname     CHAR(127) NOT NULL  COMMENT '名',
targetname       CHAR(127) NOT NULL  COMMENT '名',
operatorid       INT       NOT NULL ,
targetid         INT       NOT NULL ,
createtime       DATETIME  NOT NULL ,
action      CHAR(127) NOT NULL,
PRIMARY KEY (idtrace)
) ENGINE InnoDB CHARACTER SET UTF8;

create INDEX targetid_of_trace_index on trace(targetid);
CREATE TABLE ldapconfig
(
idldapconfig     INT       NOT NULL AUTO_INCREMENT COMMENT 'ID',
domainid         INT       NULL                    COMMENT 'LDAP配置所属的域ID',
domainsearchbase CHAR(255) NOT NULL                COMMENT '域服务器查询起点',
syncinterval     INT       NOT NULL                COMMENT '同步时间间隔秒数',
CONSTRAINT ldapconfig_domainid_domainsearchbase UNIQUE (domainid, domainsearchbase),
PRIMARY KEY (idldapconfig)
) ENGINE InnoDB CHARACTER SET UTF8;

CREATE TABLE ldapconfigstatus
(
idldapconfig INT NOT NULL COMMENT 'ID',
status       INT NOT NULL COMMENT 'LDAP配置状态. 0：正常; 1：异常; 2：同步中',
PRIMARY KEY (idldapconfig)
) ENGINE InnoDB CHARACTER SET UTF8;

CREATE TABLE organization
(
idorganization   INT       NOT NULL AUTO_INCREMENT COMMENT 'ID',
organizationtype INT       NOT NULL                COMMENT '0x0为本地组织; 0x1为MSAD同步组织单元; 0x2为ApacheDS同步组织单元',
guid             CHAR(127) NOT NULL                COMMENT 'GUID',
domainid         INT       NOT NULL                COMMENT '组织所属的域',
organizationname CHAR(127) NOT NULL                COMMENT '组织名称',
level            INT       NOT NULL                COMMENT '组织级别，顶层组织的组织级别为0',
parent           INT       NOT NULL                COMMENT '上一级组织ID，顶层组织的上一级组织为-1',
notes            CHAR(255) NOT NULL                COMMENT '备注',
CONSTRAINT organization_domainid_guid UNIQUE (domainid, guid),
PRIMARY KEY (idorganization)
) ENGINE InnoDB CHARACTER SET UTF8;

CREATE TABLE user
(
iduser                INT       NOT NULL AUTO_INCREMENT COMMENT 'ID',
usertype              INT       NOT NULL                COMMENT '0x0为本地用户; 0x1为MSAD同步用户; 0x2为ApacheDS同步用户',
guid                  CHAR(127) NOT NULL                COMMENT 'GUID',
domainid              INT       NOT NULL                COMMENT '用户所属的域ID',
organizationid        INT       NULL                    COMMENT '用户所属的组织ID',
username              CHAR(127) NOT NULL                COMMENT '用户名，用户登录系统使用的名称',
password              CHAR(127) NOT NULL                COMMENT '口令，加密存储',
realname              CHAR(127) NOT NULL                COMMENT '真实姓名',
idcardtype            CHAR(127) NOT NULL                COMMENT '证件类型',
idcard                CHAR(127) NOT NULL                COMMENT '证件号码',
email                 CHAR(127) NOT NULL                COMMENT '邮件地址',
address               CHAR(127) NOT NULL                COMMENT '地址',
telephone             CHAR(127) NOT NULL                COMMENT '电话',
notes                 CHAR(255) NOT NULL                COMMENT '备注',
domainname            CHAR(127) NOT NULL                COMMENT '',
organizationname      CHAR(127) NULL                    COMMENT '',
deleted               INT       NOT NULL                COMMENT '',
CONSTRAINT user_username_domainid UNIQUE (guid, domainid, deleted),
PRIMARY KEY (iduser)
) ENGINE InnoDB CHARACTER SET UTF8;

CREATE TABLE rsakey
(
idrsakey  INT       NOT NULL AUTO_INCREMENT COMMENT 'ID',
rsatype   INT       NOT NULL                COMMENT '',
keyid     CHAR(127) NOT NULL                COMMENT '',
pdata     TEXT      NOT NULL                COMMENT '',
ownerid   INT       NOT NULL                COMMENT '',
ownername CHAR(127) NULL                    COMMENT '',
disabled  INT       NOT NULL                COMMENT '',
CONSTRAINT rsakey_keyid UNIQUE (rsatype, keyid),
CONSTRAINT rsakey_ownerid UNIQUE (ownerid),
PRIMARY KEY (idrsakey)
) ENGINE InnoDB CHARACTER SET UTF8;

CREATE TABLE uservolume
(
iduservolume   INT       NOT NULL AUTO_INCREMENT COMMENT 'ID',
userid         INT       NOT NULL                COMMENT '用户ID',
cloudmanagerid INT       NOT NULL                COMMENT '个人存储所使用的虚拟平台的ID',
storageid      CHAR(127) NOT NULL                COMMENT '个人存储ID',
volumename     CHAR(127) NOT NULL                COMMENT '',
totalsize      BIGINT    NULL                    COMMENT '',
usedsize       BIGINT    NULL                    COMMENT '',
desktoppoolid  INT       NULL                    COMMENT '',
desktopid      INT       NULL                    COMMENT '',
lastdetachtime BIGINT    NULL                    COMMENT '',
PRIMARY KEY (iduservolume)
) ENGINE InnoDB CHARACTER SET UTF8;

CREATE TABLE groups
(
idgroup   INT       NOT NULL AUTO_INCREMENT COMMENT 'ID',
groupname CHAR(255) NOT NULL                COMMENT '组名',
domainid  INT       NOT NULL                COMMENT '组所属的域ID',
notes     CHAR(255) NOT NULL                COMMENT '备注',
CONSTRAINT groups_groupid_groupname UNIQUE (groupname),
PRIMARY KEY (idgroup)
) ENGINE InnoDB CHARACTER SET UTF8;

CREATE TABLE groupelement
(
idgroupelement INT NOT NULL AUTO_INCREMENT COMMENT 'ID',
groupid        INT NOT NULL                COMMENT '组ID',
elementid      INT NOT NULL                COMMENT '元素ID',
elementtype    INT NOT NULL                COMMENT '元素类型. 0x11：用户; 0x12：组织; 0x20：组（预留）',
CONSTRAINT groupelement_groupid_elementid_elementtype UNIQUE (groupid, elementid, elementtype),
PRIMARY KEY (idgroupelement)
) ENGINE InnoDB CHARACTER SET UTF8;

CREATE TABLE administrator
(
idadministrator INT NOT NULL AUTO_INCREMENT COMMENT 'ID',
userid          INT NOT NULL                COMMENT '管理员userid',
targettype      INT NOT NULL                COMMENT '管理对象类型. 0x00：全局管理员; 0x11：用户; 0x12：组织; 0x20：组',
targetid        INT NOT NULL                COMMENT '管理对象id',
CONSTRAINT administrator_constraint UNIQUE (userid, targettype, targetid),
PRIMARY KEY (idadministrator)
) ENGINE InnoDB CHARACTER SET UTF8;

CREATE TABLE session
(
idsession              INT       NOT NULL AUTO_INCREMENT COMMENT 'ID',
userid                 INT       NOT NULL                COMMENT '用户ID',
logintype              INT       NOT NULL                COMMENT '登录身份. 0x00：全局管理员; 0x11：用户',
ticket                 CHAR(127) NOT NULL                COMMENT '登录令牌',
expire                 DATETIME  NOT NULL                COMMENT '有效截至时间',
password               CHAR(127) NOT NULL                COMMENT 'TODO Check HLD',
source                 CHAR(32)  NULL                COMMENT 'source ip',
clienttype             CHAR(200)  NULL                COMMENT 'client type',  
deleted                INT       NOT NULL                COMMENT '',
invalidatingreasoncode INT       NOT NULL                COMMENT '',
PRIMARY KEY (idsession)
) ENGINE MyISAM CHARACTER SET UTF8;

CREATE TABLE cloudmanager
(
idcloudmanager  INT       NOT NULL AUTO_INCREMENT COMMENT 'ID',
cloudname       CHAR(127) NOT NULL                COMMENT '虚拟化管理平台名称',
baseurl         CHAR(255) NOT NULL                COMMENT 'API路径',
username        CHAR(127) NOT NULL                COMMENT '管理员用户名',
password        CHAR(127) NOT NULL                COMMENT '管理员口令',
domain          CHAR(127) NOT NULL                COMMENT '管理员域',
clouddrivername CHAR(127) NOT NULL                COMMENT 'Cloudstack/Openstack/OpzoonCloud',
notes           CHAR(255) NOT NULL                COMMENT '备注',
CONSTRAINT cloudmanager_baseurl UNIQUE (baseurl),
CONSTRAINT cloudmanager_cloudname UNIQUE (cloudname),
PRIMARY KEY (idcloudmanager)
) ENGINE InnoDB CHARACTER SET UTF8;

CREATE TABLE desktoppool
(
iddesktoppool       INT       NOT NULL AUTO_INCREMENT COMMENT 'ID',
poolname            CHAR(127) NOT NULL                COMMENT '桌面池名称',
vmsource            INT       NOT NULL                COMMENT '桌面来源. 0：自动池，虚拟桌面从模板部署; 1：手动池，虚拟桌面系手动添加的虚拟机',
assignment          INT       NOT NULL                COMMENT '虚拟桌面分配方式. 0：浮动，用户注销后不保留分配关系; 1：固定，用户将保留首次分配的虚拟桌面',
cloudmanagerid      INT       NOT NULL                COMMENT '虚拟化管理平台ID',
templateid          CHAR(255) NULL                    COMMENT '模板ID（手动池为空）',
templatename        CHAR(127) NULL                    COMMENT '模板名称（冗余）（手动池为空）',
vmnamepatterrn      CHAR(127) NOT NULL                COMMENT '虚拟机名称命名模式',
computernamepattern CHAR(127) NOT NULL                COMMENT '计算机名称命名模式',
domainname          CHAR(127) NULL                    COMMENT '域名称',
domainbinddn        CHAR(127) NULL                    COMMENT '域服务器管理员用户名',
domainbindpass      CHAR(127) NULL                    COMMENT '域服务器管理员口令',
maxdesktops         INT       NOT NULL                COMMENT '最大桌面数',
sparedesktops       INT       NOT NULL                COMMENT '热备桌面数',
notes               CHAR(255) NOT NULL                COMMENT '备注',
availableprotocols  INT       NOT NULL                COMMENT '',
unassignmentdelay   INT       NOT NULL                COMMENT '',
link                TINYINT(1) NOT NULL                COMMENT '是否是link clone',
CONSTRAINT desktoppool_constraint UNIQUE (poolname),
CONSTRAINT desktoppool_constraint_1 UNIQUE (computernamepattern),
PRIMARY KEY (iddesktoppool)
) ENGINE InnoDB CHARACTER SET UTF8;

CREATE TABLE desktop
(
iddesktop       INT       NOT NULL AUTO_INCREMENT COMMENT 'ID',
desktoppoolid   INT       NOT NULL                COMMENT '桌面池ID',
vmid            CHAR(127) NULL                    COMMENT '虚拟机ID',
vmname          CHAR(255) NOT NULL                COMMENT '虚拟机名称',
ownerid         INT       NOT NULL                COMMENT '分配到用户ID，未分配的桌面或浮动池中的桌面为-1',
ipaddress       CHAR(127) NULL                    COMMENT '',
desktoppoolname CHAR(127) NOT NULL                COMMENT '桌面池名称',
ownername       CHAR(127) NULL                    COMMENT '',
realname       CHAR(127) NULL                    COMMENT '',
vmsource        INT       NOT NULL                COMMENT '桌面来源. 0：自动池，虚拟桌面从模板部署; 1：手动池，虚拟桌面系手动添加的虚拟机',
assignment      INT       NOT NULL                COMMENT '虚拟桌面分配方式. 0：浮动，用户注销后不保留分配关系; 1：固定，用户将保留首次分配的虚拟桌面',
CONSTRAINT desktop_constraint UNIQUE (ipaddress),
PRIMARY KEY (iddesktop)
) ENGINE InnoDB CHARACTER SET UTF8;

CREATE TABLE resourceassignment
(
idresourceassignment INT       NOT NULL AUTO_INCREMENT COMMENT 'ID',
resourcetype         INT       NOT NULL                COMMENT '资源类别: 0x001：虚拟桌面池; 0x201：虚拟应用',
resourceid           INT       NOT NULL                COMMENT '资源ID',
visitortype          INT       NOT NULL                COMMENT '访问者类别: 0x11：用户; 0x12：组织; 0x20：组',
visitorid            INT       NOT NULL                COMMENT '访问者ID',
permission           INT       NOT NULL                COMMENT '许可类型（保留，目前使用白名单）: 0：禁止; 1：允许',
CONSTRAINT resourceassignment_constraint UNIQUE (resourcetype, resourceid, visitortype, visitorid),
PRIMARY KEY (idresourceassignment)
) ENGINE InnoDB CHARACTER SET UTF8;

CREATE TABLE restrictionstrategy
(
idrestrictionstrategy INT       NOT NULL AUTO_INCREMENT COMMENT 'ID',
strategyname          CHAR(127) NOT NULL                COMMENT '',
clipboard             INT       NOT NULL                COMMENT '',
usbenabled            INT       NOT NULL                COMMENT '',
disk                  INT       NOT NULL                COMMENT '',
audio                 INT       NOT NULL                COMMENT '',
uservolume            INT       NOT NULL                COMMENT '',
notes                 CHAR(255) NOT NULL                  COMMENT '备注',
/**hasusbclasswhitelist  INT       NOT NULL                COMMENT '',
hasusbclassblacklist  INT       NOT NULL                COMMENT '',
hasusbdevicewhitelist INT       NOT NULL                COMMENT '',
hasusbdeviceblacklist INT       NOT NULL                COMMENT '',*/
CONSTRAINT restrictionstrategy_constraint UNIQUE (strategyname),
PRIMARY KEY (idrestrictionstrategy)
) ENGINE InnoDB CHARACTER SET UTF8;

INSERT INTO restrictionstrategy (strategyname, clipboard, usbenabled) VALUES ('default', 0, 0);
update restrictionstrategy SET idrestrictionstrategy = 0;

CREATE TABLE usblistitem
(
idusblistitem         INT       NOT NULL AUTO_INCREMENT COMMENT 'ID',
restrictionstrategyid INT       NOT NULL                COMMENT '',
listtype              INT       NOT NULL                COMMENT '',
itemname              CHAR(127) NOT NULL                COMMENT '',
class                 INT       NULL                    COMMENT '',
subclass              INT       NULL                    COMMENT '',
protocol              INT       NULL                    COMMENT '',
venderid              CHAR(127) NULL                    COMMENT '',
productid             CHAR(127) NULL                    COMMENT '',
PRIMARY KEY (idusblistitem)
) ENGINE InnoDB CHARACTER SET UTF8;

CREATE TABLE restrictionstrategyassignment
(
idrestrictionstrategyassignment INT NOT NULL AUTO_INCREMENT COMMENT 'ID',
restrictionstrategyid           INT NOT NULL                COMMENT '',
targettype                      INT NOT NULL                COMMENT '',
targetid                        INT NOT NULL                COMMENT '',
domainid                        INT NOT NULL                COMMENT '',
CONSTRAINT rsa_constraint UNIQUE (restrictionstrategyid, targettype, targetid),
PRIMARY KEY (idrestrictionstrategyassignment)
) ENGINE InnoDB CHARACTER SET UTF8;

CREATE TABLE cloudmanagerstatus
(
idcloudmanager INT NOT NULL COMMENT 'ID',
phase          INT NOT NULL COMMENT '',
status         INT NOT NULL COMMENT '',
PRIMARY KEY (idcloudmanager)
) ENGINE MyISAM CHARACTER SET UTF8;

CREATE TABLE desktoppoolstatus
(
iddesktoppool    INT NOT NULL COMMENT 'ID',
phase            INT NOT NULL COMMENT '',
status           INT NOT NULL COMMENT '桌面池状态. 0：正常服务状态; 1：维护状态，不可连接; 2：满，不再接纳新的连接',
abnormaldesktops INT NOT NULL COMMENT '问题桌面数',
sparingdesktops  INT NOT NULL COMMENT '当前热备桌面数',
PRIMARY KEY (iddesktoppool)
) ENGINE MyISAM CHARACTER SET UTF8;

CREATE TABLE desktopstatus
(
iddesktop INT NOT NULL COMMENT 'ID',
phase     INT NOT NULL COMMENT '',
status    INT NOT NULL COMMENT '桌面状态. 0：关闭; 1：启动但不提供服务; 2：可提供RDP服务，但未连接; 3：已连接; 256：问题桌面，需要管理员处理',
connected INT NOT NULL COMMENT '',
ownerid   INT NOT NULL COMMENT '分配的用户ID，尚未分配的为-1，浮动池与数据库中的数据有差别',
PRIMARY KEY (iddesktop)
) ENGINE MyISAM CHARACTER SET UTF8;

CREATE TABLE asyncjob
(
jobid         INT       NOT NULL AUTO_INCREMENT COMMENT 'ID',
cmd           CHAR(127) NOT NULL                COMMENT '发起任务的命令',
createtime    DATETIME  NOT NULL                COMMENT '发起任务的时间',
jobstatus     CHAR(127) NOT NULL                COMMENT '任务状态代码: 0：任务仍在执行; 1：任务执行成功，jobresultcode为0; 2：任务执行失败，jobresultcode返回错误代码',
jobprocstatus INT       NOT NULL                COMMENT '任务进度信息，0~100',
handle        CHAR(255) NOT NULL                COMMENT '执行任务的线程/对象',
jobresultcode INT       NOT NULL                COMMENT '任务结束代码',
jobresult     CHAR(255) NOT NULL                COMMENT '任务结果',
PRIMARY KEY (jobid)
) ENGINE MyISAM CHARACTER SET UTF8;

CREATE TABLE connection
(
idconnection     INT       NOT NULL AUTO_INCREMENT COMMENT 'ID',
sessionid        INT       NOT NULL                COMMENT '会话ID',
resourcetype     INT       NOT NULL                COMMENT '访问资源类别. 0x001：虚拟桌面池; 0x101：虚拟应用',
resourceid       INT       NOT NULL                COMMENT '访问资源ID',
hostname         CHAR(127) NOT NULL                COMMENT '目标主机',
hostport         INT       NOT NULL                COMMENT '目标主机端口',
brokername       CHAR(127) NOT NULL                COMMENT '转发主机',
brokerport       INT       NOT NULL                COMMENT '转发主机端口',
brokerprotocol   INT       NOT NULL                COMMENT '协议. 0：无限制协议; 1：HTTP协议封装; 2：HTTPS协议封装',
connectionticket CHAR(127) NOT NULL                COMMENT '连接令牌',
expire           DATETIME  NOT NULL                COMMENT '有效截至时间',
tunnelname       CHAR(127) NULL                    COMMENT '',
tunnelport       INT       NULL                    COMMENT '',
PRIMARY KEY (idconnection)
) ENGINE MyISAM CHARACTER SET UTF8;

CREATE TABLE floatingdesktopexpire
(
idexpire      INT       NOT NULL AUTO_INCREMENT COMMENT 'ID',
desktoppoolid INT       NOT NULL                COMMENT 'ID',
desktopid     INT       NOT NULL                COMMENT 'ID',
expire        DATETIME  NOT NULL                COMMENT '有效截至时间',
CONSTRAINT expire_constraint UNIQUE (desktopid),
PRIMARY KEY (idexpire)
) ENGINE MyISAM CHARACTER SET UTF8;

CREATE TABLE runtimevariable
(
idruntimevariable INT       NOT NULL AUTO_INCREMENT COMMENT 'ID',
name              CHAR(127) NOT NULL                COMMENT '变量名称',
value             CHAR(127) NOT NULL                COMMENT '变量值',
PRIMARY KEY (idruntimevariable)
) ENGINE MyISAM CHARACTER SET UTF8;

CREATE TABLE notification
(
idnotification INT       NOT NULL AUTO_INCREMENT COMMENT 'ID',
userid         INT       NOT NULL                COMMENT '',
sessionid      INT       NOT NULL                COMMENT '',
major          INT       NOT NULL                COMMENT '',
minor          INT       NOT NULL                COMMENT '',
level          INT       NOT NULL                COMMENT '',
parameter      CHAR(127) NOT NULL                COMMENT '',
PRIMARY KEY (idnotification)
) ENGINE MyISAM CHARACTER SET UTF8;

CREATE TABLE `license` (
  `idlicense` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `title` varchar(100) NOT NULL COMMENT 'license文件名称',
  `content` varchar(500) NOT NULL COMMENT 'license加密字符串',
  `createtime` varchar(50) NOT NULL COMMENT '导入license时间',
  `type` int(1) NOT NULL COMMENT '类型:0=默认/1=正式/2=试用',
  `mode` int(1) NOT NULL COMMENT '模式:0=Ukey/1=单机/2=集群',
  `expire` int(5)  NOT NULL COMMENT '有效天数',
  `connectCount` varchar(5) COMMENT '',
  PRIMARY KEY (`idlicense`),
  UNIQUE KEY `uktitle` (`title`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `licenseserver` (
  `idlicenseserver` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `ip` varchar(20) NOT NULL COMMENT 'license sever ip',
  `port` varchar(10) NOT NULL COMMENT 'license sever 端口 ',
  PRIMARY KEY (`idlicenseserver`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE VIEW resourceassignmentwithresourceview AS
SELECT resourceid * 0x1000 + resourcetype resource, ra.*,
    p.poolname resourcename,
    CASE ra.visitortype WHEN 0x11 THEN u.username WHEN 0x12 THEN o.organizationname ELSE g.groupname END visitorname
    FROM resourceassignment ra
    left join desktoppool p on ra.resourceid = p.iddesktoppool
    left join user u on ra.visitortype = 0x11 and ra.visitorid = u.iduser
    left join organization o on ra.visitortype = 0x12 and ra.visitorid = o.idorganization
    left join groups g on ra.visitortype = 0x20 and ra.visitorid = g.idgroup;

INSERT INTO domain (iddomain, domaintype, guid, domainname, domainnetworkname, domainservername, domainbinddn, domainbindpass, notes) VALUES
                   (0,        0,          '',   '',         '',                '',               '',           '',             '');
update domain SET iddomain = 0;

INSERT INTO organization (idorganization, organizationtype, guid, domainid, organizationname, level, parent, notes) VALUES
                         (0,              0,                '',   0,        '',               0,     -1,     'notes');
update organization SET idorganization = 0;

INSERT INTO user (iduser, usertype, guid, domainid, organizationid, username, password,                           realname, idcardtype, idcard,               email,              address,   telephone,     notes) VALUES
                 (0,      0,        '',   0,        0,              'admin',  '96e79218965eb72c92a549dd5a330112', 'Admin',  'idcard',   '111111111111111111', 'admin@opzoon.com', 'address', '13999999999', 'notes');
update user SET iduser = 0;

INSERT INTO administrator (idadministrator, userid, targettype, targetid) VALUES
                          (1,               0,      0,          0);

INSERT INTO runtimevariable (idruntimevariable, name,               value) VALUES
                            (1,                 'service.disabled', 'n');

/*Table structure for tables rail.  autor：马晓超*/

DROP TABLE IF EXISTS `applicationtogroup`;

CREATE TABLE `applicationtogroup` (
  `applicationid` varchar(100) DEFAULT NULL COMMENT '虚拟应用ID',
  `groupid` int(11) DEFAULT NULL COMMENT '组织ID',
  `id` varchar(100) NOT NULL COMMENT 'ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `applicationtoorganization` */

DROP TABLE IF EXISTS `applicationtoorganization`;

CREATE TABLE `applicationtoorganization` (
  `applicationid` varchar(100) DEFAULT NULL COMMENT '虚拟应用ID',
  `organizationid` int(11) DEFAULT NULL COMMENT '组织ID',
  `id` varchar(100) NOT NULL COMMENT '主键',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `applicationtouser` */

DROP TABLE IF EXISTS `applicationtouser`;

CREATE TABLE `applicationtouser` (
  `applicationid` varchar(100) NOT NULL COMMENT '虚拟应用ID',
  `userid` int(11) NOT NULL COMMENT '用户ID',
  `id` varchar(100) NOT NULL COMMENT '主键',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `railapplication` */

DROP TABLE IF EXISTS `railapplication`;

CREATE TABLE `railapplication` (
  `idrailapplication` int(19) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `applicationname` varchar(50) DEFAULT NULL COMMENT '虚拟应用名称',
  `applicationversion` varchar(100) DEFAULT NULL COMMENT '版本号',
  `applicationpath` varchar(500) DEFAULT NULL COMMENT '路径',
  `applicationicon` varchar(2000) DEFAULT NULL COMMENT '图片',
  `applicationid` varchar(100) DEFAULT NULL COMMENT 'MD5 CHECKSUM',
  `applicationserverid` int(19) DEFAULT NULL COMMENT '虚拟应用服务器ID',
  `status` int(11) DEFAULT NULL COMMENT '状态',
  `published` tinyint(1) DEFAULT NULL COMMENT '是否发布',
  `servername` varchar(100) DEFAULT NULL COMMENT '虚拟应用服务器的地址信息',
  `applicationarguments` varchar(500) DEFAULT NULL COMMENT '虚拟应用运行的参数',
  PRIMARY KEY (`idrailapplication`) 
) ENGINE=InnoDB AUTO_INCREMENT=1388 DEFAULT CHARSET=utf8;


/*Table structure for table `railapplicationserver` */

DROP TABLE IF EXISTS `railapplicationserver`;

CREATE TABLE `railapplicationserver` (
  `idapplicationserver` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `servertype` int(11) DEFAULT NULL COMMENT '服务类型',
  `servername` varchar(127) DEFAULT NULL COMMENT '虚拟应用服务器的地址信息',
  `timespan` int(11) DEFAULT NULL COMMENT '时间戳',
  `cpuload` varchar(127) DEFAULT NULL COMMENT 'cpu 负载',
  `networkload` varchar(127) DEFAULT NULL COMMENT '网络',
  `memload` varchar(127) DEFAULT NULL COMMENT '内存',
  `connections` int(11) DEFAULT NULL COMMENT '连接数',
  `appinstalled` int(11) DEFAULT NULL COMMENT '安装数',
  `apppublished` int(11) DEFAULT NULL COMMENT '发布数',
  `status` int(11) DEFAULT NULL COMMENT '状态',
  `notes` varchar(255) DEFAULT NULL COMMENT '备注',
  `jointype` varchar(50) DEFAULT NULL COMMENT '域类型',
  `joinname` varchar(50) DEFAULT NULL COMMENT '域',
  PRIMARY KEY (`idapplicationserver`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;



/*Table structure for table `railapplicationview` */

DROP TABLE IF EXISTS `railapplicationview`;

CREATE TABLE `railapplicationview` (
  `applicationname` varchar(50) DEFAULT NULL COMMENT '虚拟应用名称',
  `applicationversion` varchar(100) DEFAULT NULL COMMENT '版本',
  `applicationicon` varchar(2000) DEFAULT NULL COMMENT '保留字段',
  `applicationid` varchar(100) NOT NULL COMMENT 'MD5 CHECKSUM',
  `published` tinyint(1) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `replication` int(19) DEFAULT NULL COMMENT '应用服务器数量',
  PRIMARY KEY (`applicationid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE IF EXISTS `userandresourcestatus`;

CREATE TABLE `userandresourcestatus` (
  `userid` int(11) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `applicationid` varchar(100) DEFAULT NULL,
  `id` varchar(200) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `userandresourcestatus_userid_index` (`userid`),
  KEY `userandresourcestatus_applicationid_index` (`applicationid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

/* ****************************rail tables create sql structure  end. ******************************** */

-- ----------------------------------------------------------------------------------------------------------------
-- Table structure for `appstatusnode`
-- ----------------------------------------------------------------------------------------------------------------
DROP TABLE IF EXISTS `appstatusnode`;
CREATE TABLE `appstatusnode` (
  `id` bigint(19) NOT NULL AUTO_INCREMENT,
  `nodeAddress` varchar(100) DEFAULT NULL,
  `clusterState` varchar(20) DEFAULT NULL,
  `nodeState` int(11) DEFAULT NULL,
  `authentication` varchar(500) DEFAULT NULL,
  `senderAddress` varchar(50) DEFAULT NULL,
  `descInfo` varchar(500) DEFAULT NULL,
  `clusterConfigure` varchar(500) DEFAULT NULL,
  `master` tinyint(1) DEFAULT NULL,
  `serviceType` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=243 DEFAULT CHARSET=utf8;

-- ----------------------------------------------------------------------------------------------------------------
-- Table structure for `task`
-- ----------------------------------------------------------------------------------------------------------------
DROP TABLE IF EXISTS `task`;
CREATE TABLE `task` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `executorClass` varchar(1024) NOT NULL COMMENT 'Ã¦â€°Â§Ã¨Â¡Å’Ã§Â±Â»Ã¥â€¦Â¨Ã¥ÂÂ',
  `status` int(11) NOT NULL COMMENT 'Ã¤Â»Â»Ã¥Å Â¡Ã§Å Â¶Ã¦â‚¬Â. 0Ã¯Â¼Å¡Ã¥Ë†ÂÃ¥Â§â€¹Ã¥Å’â€“; 1Ã¯Â¼Å¡Ã¦Å“â€°Ã¦Å“ÂÃ¥Å Â¡Ã¥â„¢Â¨Ã¦Å½Â¥Ã¦â€Â¶; 2Ã¯Â¼Å¡Ã¦Â­Â£Ã¥Å“Â¨Ã¦â€°Â§Ã¨Â¡Å’; 3Ã¯Â¼Å¡Ã¦â€°Â§Ã¨Â¡Å’Ã¥Â®Å’Ã¦Ë†Â; 256Ã¯Â¼Å¡Ã¦â€°Â§Ã¨Â¡Å’Ã¥Ââ€˜Ã§â€Å¸Ã©â€â„¢Ã¨Â¯Â¯',
  `executeStatus` int(11) DEFAULT NULL COMMENT 'Ã¤Â»Â»Ã¥Å Â¡Ã¦â€°Â§Ã¨Â¡Å’Ã§Å Â¶Ã¦â‚¬Â. Ã§â€Â±Ã¦â€°Â§Ã¨Â¡Å’Ã§Â±Â»Ã¨â€¡ÂªÃ¥Â®Å¡Ã¤Â¹â€°',
  `executor` char(127) DEFAULT NULL COMMENT 'Ã¤Â»Â»Ã¥Å Â¡Ã¦Å½Â¥Ã¦â€Â¶Ã¨â‚¬â€¦',
  `sender` char(127) DEFAULT NULL COMMENT 'Ã¤Â»Â»Ã¥Å Â¡Ã¥Ââ€˜Ã¥â€¡ÂºÃ¨â‚¬â€¦',
  `para1` varchar(1024) DEFAULT NULL COMMENT 'Ã¥Ââ€šÃ¦â€¢Â°1',
  `para2` varchar(1024) DEFAULT NULL COMMENT 'Ã¥Ââ€šÃ¦â€¢Â°2',
  `para3` varchar(1024) DEFAULT NULL COMMENT 'Ã¥Ââ€šÃ¦â€¢Â°3',
  `para4` varchar(1024) DEFAULT NULL COMMENT 'Ã¥Ââ€šÃ¦â€¢Â°4',
  `para5` varchar(1024) DEFAULT NULL COMMENT 'Ã¥Ââ€šÃ¦â€¢Â°5',
  `error` varchar(1024) DEFAULT NULL COMMENT 'Ã©â€â„¢Ã¨Â¯Â¯Ã¤Â¿Â¡Ã¦ÂÂ¯',
  `createTime` datetime DEFAULT NULL COMMENT 'Ã¥Ââ€˜Ã¨ÂµÂ·Ã¤Â»Â»Ã¥Å Â¡Ã§Å¡â€žÃ¦â€”Â¶Ã©â€”Â´',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=34 DEFAULT CHARSET=utf8;
create or replace view desktopstatusconnectionview as select desktopstatus.*,connection.idconnection from connection join session  on session.idsession=connection.sessionid  join user on session.userid=user.iduser join desktop on user.username=desktop.ownername join   desktopstatus on desktop.iddesktop=desktop.iddesktop where resourcetype=1 and desktopstatus.ownerid<>-1;
-- ----------------------------------------------------------------------------------------------------------------
-- Table structure for `publickey`
-- ----------------------------------------------------------------------------------------------------------------
DROP TABLE IF EXISTS `publickey`;
CREATE TABLE `publickey` (
  `idpublickey` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `content` varchar(1024) NOT NULL COMMENT 'æ‰§è¡Œç±»å…¨å',
  PRIMARY KEY (`idpublickey`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
