CREATE TABLE public.entries (
	id bigserial,
	"name" varchar(200) NOT NULL,
	description varchar(1000) NULL,
	amount numeric(12, 2) NOT NULL,
	category_id int8 NOT NULL,
	"date" date NOT NULL,
	"type" varchar(20) NOT NULL,
	paid bool NOT NULL,
	created_at timestamp(6) NOT NULL,
	updated_at timestamp(6) NULL,
	CONSTRAINT entries_pkey PRIMARY KEY (id),
	CONSTRAINT entries_categories_fkey FOREIGN KEY (category_id) REFERENCES public.categories(id)
);
CREATE INDEX idx_entry_category ON public.entries USING btree (category_id);
CREATE INDEX idx_entry_date ON public.entries USING btree (date);
CREATE INDEX idx_entry_paid ON public.entries USING btree (paid);
CREATE INDEX idx_entry_type ON public.entries USING btree (type);