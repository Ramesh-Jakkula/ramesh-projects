create database airline_user_db;
create database airline_location_db;
create database airline_core_db;
create database airline_flight_db;
create database airline_seat_db;
create database airline_pricing_db;
create database airline_ancillary_db;
create database airline_booking_db;
create database airline_payment_db;

use airline_user_db;
use airline_location_db;
use airline_core_db;
use airline_flight_db;
use airline_seat_db;
use airline_pricing_db;
use airline_ancillary_db;
use airline_booking_db;
use airline_payment_db;

show tables;

select * from user; 
select * from city; 
select * from airport; 
select * from airline; 
select * from aircraft;
select * from flight;
select * from flight_schedule;
select * from flight_instance;
select * from flight_schedule_operating_days;
select * from cabin_class;
select * from seat_map;
select * from seat;
select * from flight_instance_cabin;
select * from seat_instance;
select * from fare;
select * from fare_rules;
select * from baggage_policy;
select * from meal;
select * from ancillary;
select * from insurance_coverage;
select * from flight_meal;
select * from flight_cabin_ancillary;
select * from booking;
select * from passenger;
select * from ticket;
select * from payment;

