

--
-- Name: business_entity; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.business_entity (
    is_active boolean NOT NULL,
    created_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone,
    id uuid NOT NULL,
    user_id uuid,
    business_type character varying(255) NOT NULL,
    description character varying(255),
    name character varying(255) NOT NULL,
    CONSTRAINT business_entity_business_type_check CHECK (((business_type)::text = ANY ((ARRAY['GYM'::character varying, 'TUTION'::character varying, 'SWIMMING_CLASS'::character varying, 'RENT'::character varying, 'OTHER'::character varying])::text[])))
);


--
-- Name: member_entity; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.member_entity (
    membership_fee numeric(38,2) NOT NULL,
    created_at timestamp(6) without time zone,
    date_of_birth timestamp(6) without time zone,
    membership_end timestamp(6) without time zone,
    membership_start timestamp(6) without time zone,
    updated_at timestamp(6) without time zone,
    business_entity_id uuid,
    id uuid NOT NULL,
    address character varying(255),
    created_by character varying(255),
    email character varying(255),
    emergency_contact_name character varying(255),
    emergency_contact_phone character varying(255),
    full_name character varying(255) NOT NULL,
    gender character varying(255),
    member_code_auto_generated character varying(255),
    member_id_of_business character varying(255),
    membership_plan character varying(255) NOT NULL,
    notes character varying(255),
    phone character varying(255),
    status character varying(255),
    updated_by character varying(255),
    CONSTRAINT member_entity_membership_plan_check CHECK (((membership_plan)::text = ANY ((ARRAY['MONTHLY'::character varying, 'DAILY'::character varying, 'WEEKLY'::character varying, 'QUARTERLY'::character varying, 'HOURLY'::character varying, 'HALF_YEARLY'::character varying, 'ANNUALLY'::character varying, 'OTHERS'::character varying])::text[])))
);


--
-- Name: user_entity; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.user_entity (
    email_verified boolean,
    is_active boolean NOT NULL,
    phone_verified boolean,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone,
    id uuid NOT NULL,
    auth_provider character varying(255),
    email character varying(255) NOT NULL,
    full_name character varying(255) NOT NULL,
    password_hash character varying(255) NOT NULL,
    phone character varying(255),
    profile_photo_url character varying(255),
    provider_id character varying(255),
    CONSTRAINT user_entity_auth_provider_check CHECK (((auth_provider)::text = ANY ((ARRAY['LOCAL'::character varying, 'GOOGLE'::character varying])::text[])))
);


--
-- Name: business_entity business_entity_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.business_entity
    ADD CONSTRAINT business_entity_pkey PRIMARY KEY (id);


--
-- Name: member_entity member_entity_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.member_entity
    ADD CONSTRAINT member_entity_pkey PRIMARY KEY (id);


--
-- Name: member_entity uk_businessid_email; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.member_entity
    ADD CONSTRAINT uk_businessid_email UNIQUE (business_entity_id, email);


--
-- Name: member_entity uk_businessid_memberidofbusiness; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.member_entity
    ADD CONSTRAINT uk_businessid_memberidofbusiness UNIQUE (business_entity_id, member_id_of_business);


--
-- Name: member_entity uk_businessid_phone; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.member_entity
    ADD CONSTRAINT uk_businessid_phone UNIQUE (business_entity_id, phone);


--
-- Name: user_entity user_entity_email_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_entity
    ADD CONSTRAINT user_entity_email_key UNIQUE (email);


--
-- Name: user_entity user_entity_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_entity
    ADD CONSTRAINT user_entity_pkey PRIMARY KEY (id);


--
-- Name: member_entity fk_member_business; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.member_entity
    ADD CONSTRAINT fk_member_business FOREIGN KEY (business_entity_id) REFERENCES public.business_entity(id) ON DELETE CASCADE;


--
-- Name: business_entity fkm37hqjro0aq71hi0x3nrdnqx7; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.business_entity
    ADD CONSTRAINT fkm37hqjro0aq71hi0x3nrdnqx7 FOREIGN KEY (user_id) REFERENCES public.user_entity(id);


--
-- PostgreSQL database dump complete
--


