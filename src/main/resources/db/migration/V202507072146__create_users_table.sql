create table users(
	id bigserial, 
	name varchar(255) not null, 
	email varchar(100) not null, 
	password varchar(60) not null, 
	role varchar(255) not null, 
	enabled boolean not null, 
	account_non_expired boolean not null, 
	account_non_locked boolean not null, 
	credentials_non_expired boolean not null, 
	created_at timestamp, 
	updated_at timestamp, 
	last_login timestamp
);

create index idx_user_name on users(name);
create index idx_user_email on users(email);
create index idx_user_enabled on users(enabled);
