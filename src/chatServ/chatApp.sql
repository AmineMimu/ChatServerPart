-- phpMyAdmin SQL Dump
-- version 4.5.2
-- http://www.phpmyadmin.net
--
-- Client :  localhost
-- Généré le :  Dim 25 Décembre 2016 à 03:51
-- Version du serveur :  10.1.16-MariaDB
-- Version de PHP :  5.6.24

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données :  `chatApp`
--

-- --------------------------------------------------------

--
-- Structure de la table `utilisateur`
--

CREATE TABLE `utilisateur` (
  `id` int(11) NOT NULL,
  `nomUtilisateur` varchar(15) DEFAULT NULL,
  `prenomUtilisateur` varchar(15) DEFAULT NULL,
  `motDePasse` varchar(15) DEFAULT NULL,
  `historique` text,
  `emailUtilisateur` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Contenu de la table `utilisateur`
--

INSERT INTO `utilisateur` (`id`, `nomUtilisateur`, `prenomUtilisateur`, `motDePasse`, `historique`, `emailUtilisateur`) VALUES
(3, 'Matouk', 'Amine', '1234', NULL, 'amji'),
(13, 'zoubir', 'kheloudj', '1', NULL, 'moi'),
(14, 'bdd', 'ohoo', 'mdr', NULL, 'ohoo'),
(15, '1', '2', '4', NULL, '3'),
(16, 'ami', 'nice', 'cool', NULL, 'anahowa'),
(17, 'yo', 'james', 'wassup', NULL, 'wassup'),
(18, 'ahmed', 'khan', 'y', NULL, 'yow'),
(19, 'Duffour', 'Richard', ',;ds,lkfdsf', NULL, 'zeProf');

--
-- Index pour les tables exportées
--

--
-- Index pour la table `utilisateur`
--
ALTER TABLE `utilisateur`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT pour les tables exportées
--

--
-- AUTO_INCREMENT pour la table `utilisateur`
--
ALTER TABLE `utilisateur`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
