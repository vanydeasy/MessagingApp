-- phpMyAdmin SQL Dump
-- version 4.2.11
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Nov 02, 2016 at 02:11 PM
-- Server version: 5.6.21
-- PHP Version: 5.6.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `messaging_app`
--

-- --------------------------------------------------------

--
-- Table structure for table `friend`
--

CREATE TABLE IF NOT EXISTS `friend` (
  `username` varchar(16) NOT NULL,
  `friend_username` varchar(16) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `group`
--

CREATE TABLE IF NOT EXISTS `group` (
`group_id` int(11) NOT NULL,
  `name` varchar(32) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `group`
--

INSERT INTO `group` (`group_id`, `name`) VALUES
(1, 'death_gorgeous'),
(2, 'camp_halfblood'),
(3, 'haiho'),
(4, 'haiho'),
(5, 'haiho'),
(6, 'haiho'),
(7, 'haiho');

-- --------------------------------------------------------

--
-- Table structure for table `group_member`
--

CREATE TABLE IF NOT EXISTS `group_member` (
  `group_id` int(11) NOT NULL,
  `username` varchar(16) NOT NULL,
  `isAdmin` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `group_member`
--

INSERT INTO `group_member` (`group_id`, `username`, `isAdmin`) VALUES
(1, 'patch_cipriano', 0),
(1, 'pipink', 0),
(1, 'raffeetheangel', 1),
(1, 'willherondale', 0),
(2, 'annabeth_chase', 1);

-- --------------------------------------------------------

--
-- Table structure for table `group_message`
--

CREATE TABLE IF NOT EXISTS `group_message` (
`message_id` int(11) NOT NULL,
  `group_id` int(11) NOT NULL,
  `sent_by` varchar(16) NOT NULL,
  `content` varchar(255) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `personal_message`
--

CREATE TABLE IF NOT EXISTS `personal_message` (
  `message_id` int(11) NOT NULL DEFAULT '0',
  `sent_by` varchar(16) NOT NULL,
  `sent_to` varchar(16) NOT NULL,
  `content` varchar(255) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE IF NOT EXISTS `user` (
  `username` varchar(16) NOT NULL,
  `password` varchar(16) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`username`, `password`) VALUES
('aburr', '123456'),
('annabeth_chase', '333333'),
('jasongrace', '999999'),
('patch_cipriano', '222222'),
('penryn_young', '987654'),
('percyjackson', '444444'),
('pipink', '222222'),
('raffeetheangel', '333333'),
('valdezleo', '111111'),
('vanydeasy', '111111'),
('willherondale', '123456');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `friend`
--
ALTER TABLE `friend`
 ADD PRIMARY KEY (`username`,`friend_username`), ADD KEY `friend_username` (`friend_username`);

--
-- Indexes for table `group`
--
ALTER TABLE `group`
 ADD PRIMARY KEY (`group_id`);

--
-- Indexes for table `group_member`
--
ALTER TABLE `group_member`
 ADD PRIMARY KEY (`group_id`,`username`), ADD KEY `username` (`username`), ADD KEY `group_id` (`group_id`);

--
-- Indexes for table `group_message`
--
ALTER TABLE `group_message`
 ADD PRIMARY KEY (`message_id`), ADD KEY `group_id` (`group_id`), ADD KEY `sent_by` (`sent_by`);

--
-- Indexes for table `personal_message`
--
ALTER TABLE `personal_message`
 ADD PRIMARY KEY (`message_id`), ADD KEY `sent_by` (`sent_by`), ADD KEY `sent_to` (`sent_to`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
 ADD PRIMARY KEY (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `group`
--
ALTER TABLE `group`
MODIFY `group_id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=8;
--
-- AUTO_INCREMENT for table `group_message`
--
ALTER TABLE `group_message`
MODIFY `message_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `friend`
--
ALTER TABLE `friend`
ADD CONSTRAINT `friend_ibfk_1` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT `friend_ibfk_2` FOREIGN KEY (`friend_username`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `group_member`
--
ALTER TABLE `group_member`
ADD CONSTRAINT `group_member_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `group` (`group_id`) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT `group_member_ibfk_2` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
