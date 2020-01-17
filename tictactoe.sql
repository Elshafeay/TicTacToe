-- phpMyAdmin SQL Dump
-- version 4.8.5
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jan 10, 2020 at 12:18 PM
-- Server version: 10.1.38-MariaDB
-- PHP Version: 7.3.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `tictactoe`
--

-- --------------------------------------------------------

--
-- Table structure for table `players`
--

CREATE TABLE `players` (
  `FIRSTNAME` varchar(50) NOT NULL,
  `LASTNAME` varchar(50) NOT NULL,
  `USERNAME` varchar(50) NOT NULL,
  `PASSWORD` varchar(50) NOT NULL,
  `POINTS` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `players`
--

INSERT INTO `players` (`FIRSTNAME`, `LASTNAME`, `USERNAME`, `PASSWORD`, `POINTS`) VALUES
('Moahmed', 'Elshafeay', 'elshafeay', '123456', 100),
('Omar', 'Abdo', 'omar', '123456', 200),
('Ahmed', 'Atef', 'Tefa', '123456789', 200);

-- --------------------------------------------------------

--
-- Table structure for table `savedgames`
--

CREATE TABLE `savedgames` (
  `ID` int(11) NOT NULL,
  `PLAYER1` varchar(50) NOT NULL,
  `PLAYER2` varchar(50) NOT NULL,
  `DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `BOARD` varchar(9) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `savedgames`
--

INSERT INTO `savedgames` (`ID`, `PLAYER1`, `PLAYER2`, `DATE`, `BOARD`) VALUES
(0, 'elshafeay', 'Tefa', '2020-01-07 13:02:50', 'XO_X__XOX'),
(1, 'elshafeay', 'omar', '2020-01-07 13:02:50', 'OO_XX_X_O'),
(3, 'elshafeay', 'omar', '2020-01-07 13:17:08', 'XOX__X_XO'),
(4, 'elshafeay', 'Tefa', '2020-01-07 13:05:32', 'XO_X__XOX'),
(5, 'elshafeay', 'omar', '2020-01-07 13:05:32', 'OO_XX_X_O'),
(6, 'elshafeay', 'Tefa', '2020-01-07 13:05:42', 'XO_X__XOX'),
(7, 'elshafeay', 'omar', '2020-01-07 13:05:42', 'OO_XX_X_O');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `players`
--
ALTER TABLE `players`
  ADD PRIMARY KEY (`USERNAME`);

--
-- Indexes for table `savedgames`
--
ALTER TABLE `savedgames`
  ADD PRIMARY KEY (`ID`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
