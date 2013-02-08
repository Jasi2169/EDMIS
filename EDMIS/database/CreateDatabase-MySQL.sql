/* Script to create the DataBase for the Electronic Document Management Intelligent System - EDMIS */

/* Database creation */
CREATE DATABASE IF NOT EXISTS EDMIS;

/* Select the database to tables creation */
USE EDMIS;

/* Table DocumentDataTypes creation*/
CREATE TABLE IF NOT EXISTS DocumentDataTypes(
		DocumentDataTypeID INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
		DocumentDataTypeName TEXT
	) TYPE=INNODB;
		
/* Table DocumentGroups creation */
CREATE TABLE IF NOT EXISTS DocumentGroups(
		DocumentGroupID INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
		DocumentGroupName TEXT,
		DocumentGroupDate DATETIME,
		DocumentGroupUpdate DATETIME
	) TYPE=INNODB;

/* Table SubDocumentGroups creation */
CREATE TABLE IF NOT EXISTS SubDocumentGroups(
		DocumentGroupRoot INT UNSIGNED NOT NULL,
		DocumentGroupLeaf INT UNSIGNED NOT NULL,
		PRIMARY KEY (DocumentGroupRoot, DocumentGroupLeaf),
		INDEX indDocumentGroupRoot(DocumentGroupRoot),
		FOREIGN KEY (DocumentGroupRoot) REFERENCES DocumentGroups(DocumentGroupID) ON DELETE RESTRICT,
		INDEX indDocumentGroupLeaf(DocumentGroupLeaf),
		FOREIGN KEY (DocumentGroupLeaf) REFERENCES DocumentGroups(DocumentGroupID) ON DELETE RESTRICT
	) TYPE=INNODB;

/* Table Languages creation */
CREATE TABLE IF NOT EXISTS Languages(
		LanguageID CHAR(3) NOT NULL PRIMARY KEY,
		LanguageName TEXT NOT NULL
	) TYPE=INNODB;

/* Populate Languages table */
INSERT INTO Languages (LanguageID, LanguageName) VALUES('en', 'English');
INSERT INTO Languages (LanguageID, LanguageName) VALUES('pt', 'Portuguese');

/* Table Users creation */
CREATE TABLE IF NOT EXISTS Users(
		UserID INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
		UserLogin TEXT NOT NULL,
		UserName TEXT,
		UserEmail TEXT NOT NULL,
		UserPasswd TEXT NOT NULL,
		UserLastLogin DATETIME,
		UserActive BOOL NOT NULL DEFAULT 0,
		UserAccountActive BOOL NOT NULL DEFAULT 0
	) TYPE=INNODB;

/* Table Documents creation */
CREATE TABLE IF NOT EXISTS Documents(
		DocumentID VARCHAR(32) NOT NULL PRIMARY KEY,
		DocumentName TEXT,
		DocumentSize BIGINT NOT NULL,
		DocumentFormat CHAR(5) NOT NULL,
		LanguageID CHAR(3),
		DocumentDate DATETIME,
		Document_Pages INT UNSIGNED NOT NULL DEFAULT 1,
		INDEX indLanguageID(LanguageID),
		FOREIGN KEY (LanguageID) REFERENCES Languages(LanguageID) ON DELETE RESTRICT
	) TYPE=INNODB;

/* Table DocumentData creation */
CREATE TABLE IF NOT EXISTS DocumentData(
		DocumentDataID INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
		DocumentID VARCHAR(32) NOT NULL,
		DocumentDataTypeID INT UNSIGNED NOT NULL,
		DocumentData TEXT,
		INDEX indDocumentID(DocumentID),
		FOREIGN KEY (DocumentID) REFERENCES Documents(DocumentID) ON DELETE RESTRICT,
		INDEX indDocumentDataTypeID(DocumentDataTypeID),
		FOREIGN KEY (DocumentDataTypeID) REFERENCES DocumentDataTypes(DocumentDataTypeID) ON DELETE RESTRICT
	) TYPE=INNODB;

/* Table Relations creation */
CREATE TABLE IF NOT EXISTS Relations(
		DocumentID VARCHAR(32) NOT NULL,
		DocumentGroupID INT UNSIGNED NOT NULL,
		Relation_A DOUBLE UNSIGNED NOT NULL DEFAULT 75,
		Relation_B DOUBLE UNSIGNED NOT NULL DEFAULT 50,
		Relation_E_MIN DOUBLE UNSIGNED NOT NULL DEFAULT 50,
		Relation_E_MAX DOUBLE UNSIGNED NOT NULL DEFAULT 100,
		PRIMARY KEY (DocumentID, DocumentGroupID),
		INDEX indDocumentID(DocumentID),
		FOREIGN KEY (DocumentID) REFERENCES Documents(DocumentID) ON DELETE RESTRICT,
		INDEX indDocumentGroupID(DocumentGroupID),
		FOREIGN KEY (DocumentGroupID) REFERENCES DocumentGroups(DocumentGroupID) ON DELETE RESTRICT
	) TYPE=INNODB;

/* Table PermissionTypes creation */
CREATE TABLE IF NOT EXISTS PermissionTypes(
		PermissionTypeID INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
		PermissionTypeName TEXT,
		PermissionTypeStrength INT UNSIGNED NOT NULL
	) TYPE=INNODB;

/* Table UserDataTypes creation */
CREATE TABLE IF NOT EXISTS UserDataTypes(
		UserDataTypeID INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
		UserDataTypeName TEXT
	) TYPE=INNODB;

/* Table UserData creation */
CREATE TABLE IF NOT EXISTS UserData(
		UserDataID INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
		UserID INT UNSIGNED NOT NULL,
		UserDataTypeID INT UNSIGNED NOT NULL,
		UserData TEXT,
		INDEX indUserID(UserID),
		FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE RESTRICT,
		INDEX indUserDataTypeID(UserDataTypeID),
		FOREIGN KEY (UserDataTypeID) REFERENCES UserDataTypes(UserDataTypeID) ON DELETE RESTRICT
	) TYPE=INNODB;

/* Table UserGroups creation */
CREATE TABLE IF NOT EXISTS UserGroups(
		UserGroupID INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
		UserGroupName TEXT NOT NULL
	) TYPE=INNODB;

/* Table Permissions creation */
CREATE TABLE IF NOT EXISTS Permissions(
		UserGroupID INT UNSIGNED NOT NULL,
		DocumentGroupID INT UNSIGNED NOT NULL,
		PermissionTypeID INT UNSIGNED NOT NULL,
		PRIMARY KEY (UserGroupID, DocumentGroupID, PermissionTypeID),
		INDEX indUserGroupID(UserGroupID),
		FOREIGN KEY (UserGroupID) REFERENCES UserGroups(UserGroupID) ON DELETE RESTRICT,
		INDEX indDocumentGroupID(DocumentGroupID),
		FOREIGN KEY (DocumentGroupID) REFERENCES DocumentGroups(DocumentGroupID) ON DELETE RESTRICT,
		INDEX indPermissionTypeID(PermissionTypeID),
		FOREIGN KEY (PermissionTypeID) REFERENCES PermissionTypes(PermissionTypeID) ON DELETE RESTRICT
	) TYPE=INNODB;

/* Table Views creation */
CREATE TABLE IF NOT EXISTS Views(
		UserID INT UNSIGNED NOT NULL,
		UserGroupID INT UNSIGNED NOT NULL,
		INDEX indUserID(UserID),
		FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE RESTRICT,
		INDEX indUserGroupID(UserGroupID),
		FOREIGN KEY (UserGroupID) REFERENCES UserGroups(UserGroupID) ON DELETE RESTRICT
	) TYPE=INNODB;
