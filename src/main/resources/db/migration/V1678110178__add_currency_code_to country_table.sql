DELETE FROM countries;

DROP TABLE support_data_verification_country;

alter table countries
    add column currency_code varchar(10);

INSERT countries (code, name, phone_code, currency_code) VALUES  ('ZA', 'South Africa', '+27', 'ZAR');
INSERT countries (code, name, phone_code, currency_code) VALUES  ('IE', 'Ireland', '+353', 'EUR');
INSERT countries (code, name, phone_code, currency_code) VALUES  ('GA', 'Gabon', '+241', 'XAF');
INSERT countries (code, name, phone_code, currency_code) VALUES  ('NO',  'Norway', '+47', 'NOK');
INSERT countries (code, name, phone_code, currency_code) VALUES  ('IN',  'India', '+91', 'INR');
INSERT countries (code, name, phone_code, currency_code) VALUES  ('ZM',  'Zambia', '+260', 'ZMW');
INSERT countries (code, name, phone_code, currency_code) VALUES  ('KE',  'Kenya', '+254', 'KES');
INSERT countries (code, name, phone_code, currency_code) VALUES  ('PL', 'Poland', '+48', 'PLN');
INSERT countries (code, name, phone_code, currency_code) VALUES  ('UG', 'Uganda', '+256', 'UGX');
INSERT countries (code, name, phone_code, currency_code) VALUES  ('EE', 'Estonia' ,'+372', 'EUR');
INSERT countries (code, name, phone_code, currency_code) VALUES  ('FR', 'France', '+33', 'EUR');
INSERT countries (code, name, phone_code, currency_code) VALUES  ('TZ', 'Tanzania', '+255', 'TZS');
INSERT countries (code, name, phone_code, currency_code) VALUES  ('GB', 'United Kingdom', '+44', 'GBP');
INSERT countries (code, name, phone_code, currency_code) VALUES  ('DE', 'Germany', '+49', 'EUR');
INSERT countries (code, name, phone_code, currency_code) VALUES  ('CI', 'Ivory Coast', '+225', 'XOF');
INSERT countries (code, name, phone_code, currency_code) VALUES  ('BJ', 'Benin', '+229', 'XOF');
INSERT countries (code, name, phone_code, currency_code) VALUES  ('LT', 'Lithuania', '+370', 'EUR');
INSERT countries (code, name, phone_code, currency_code) VALUES  ('CA', 'Canada', '+1', 'CAD');
INSERT countries (code, name, phone_code, currency_code) VALUES  ('ES', 'Spain', '+34', 'EUR');
INSERT countries (code, name, phone_code, currency_code) VALUES  ('LV', 'Latvia', '+371', 'EUR');
INSERT countries (code, name, phone_code, currency_code) VALUES  ('ET', 'Ethiopia', '+251', 'ETB');
INSERT countries (code, name, phone_code, currency_code) VALUES  ('PT', 'Portugal', '+351', 'EUR');
INSERT countries (code, name, phone_code, currency_code) VALUES  ('RW', 'Rwanda', '+250', 'RWF');
INSERT countries (code, name, phone_code, currency_code) VALUES  ('GH', 'Ghana', '+233', 'GHS');
INSERT countries (code, name, phone_code, currency_code) VALUES  ('NG', 'Nigeria', '+234', 'NGN');
INSERT countries (code, name, phone_code, currency_code) VALUES  ('SE', 'Sweden', '+46', 'SEK');
INSERT countries (code, name, phone_code, currency_code) VALUES  ('IT', 'Italy', '+39', 'EUR');
INSERT countries (code, name, phone_code, currency_code) VALUES  ('DK', 'Denmark', '+45', 'DKK');
INSERT countries (code, name, phone_code, currency_code) VALUES  ('NL', 'Netherlands', '+31', 'EUR');
INSERT countries (code, name, phone_code, currency_code) VALUES  ('SL', 'Sierra Leone', '+232', 'SLL');
INSERT countries (code, name, phone_code, currency_code) VALUES  ('US', 'United States of America', '+1', 'USD');



