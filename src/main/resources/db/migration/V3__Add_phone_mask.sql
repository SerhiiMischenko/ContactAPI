ALTER TABLE contacts
MODIFY COLUMN phone_number VARCHAR(20);

ALTER TABLE contacts
ADD CONSTRAINT valid_phone_number
CHECK (phone_number LIKE '+____________');
