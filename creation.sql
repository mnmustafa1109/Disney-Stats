-- create database assignment3;
use test;
DROP TABLE IF EXISTS entertainment_info;
DROP TABLE IF EXISTS entertainment_director;
DROP TABLE IF EXISTS entertainment_actor;
DROP TABLE IF EXISTS entertainment_country;
DROP TABLE IF EXISTS entertainment_genre;
DROP TABLE IF EXISTS director;
DROP TABLE IF EXISTS actor;
DROP TABLE IF EXISTS country;
DROP TABLE IF EXISTS genre;
DROP TABLE IF EXISTS entertainment;

SET character_set_server = utf8mb4;

create table entertainment(
    show_id varchar(255) primary key,
    rating varchar(255) not null,
    date_added date,
    release_year varchar(255) not null
);

create table entertainment_info(
    show_id varchar(255) not null,
    title varchar(255) not null,
    description varchar(255) ,
    primary key(show_id, title),
    duration varchar(255) not null,
    foreign key(show_id) references entertainment(show_id) on delete cascade
);

create table director(
    director_id int primary key auto_increment not null,
    director_name varchar(255) not null
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


create table entertainment_director(
    show_id varchar(255) not null,
    director_id int not null,
    foreign key(show_id) references entertainment(show_id) on delete cascade,
    foreign key(director_id) references director(director_id) on delete cascade
);

create table actor(
    actor_id int primary key auto_increment not null,
    actor_name varchar(255) not null
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

create table entertainment_actor(
    show_id varchar(255) not null,
    actor_id int not null,
    foreign key(show_id) references entertainment(show_id) on delete cascade,
    foreign key(actor_id) references actor(actor_id) on delete cascade
);

create table country(
    country_id int primary key auto_increment not null,
    country_name varchar(255) not null
);

create table entertainment_country(
    show_id varchar(255) not null,
    country_id int not null,
    foreign key(show_id) references entertainment(show_id) on delete cascade,
    foreign key(country_id) references country(country_id) on delete cascade
);

create table genre(
    genre_id int primary key auto_increment not null,
    genre_name varchar(255) not null
);

create table entertainment_genre(
    show_id varchar(255) not null,
    genre_id int not null,
    foreign key(show_id) references entertainment(show_id) on delete cascade,
    foreign key(genre_id) references genre(genre_id) on delete cascade
);
