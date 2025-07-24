CREATE TABLE public.categories (
	id bigserial,
	"name" varchar(100) NOT NULL,
	description varchar(500) NULL,
	created_at timestamp(6) NOT NULL,
	updated_at timestamp(6) NULL,
	CONSTRAINT categories_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_category_created_at ON public.categories USING btree (created_at);
CREATE INDEX idx_category_name ON public.categories USING btree (name);