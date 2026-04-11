DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'patternsAuth') THEN
        EXECUTE 'CREATE DATABASE "patternsAuth"';
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'testIdentity') THEN
        EXECUTE 'CREATE DATABASE "testIdentity"';
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'patternsUserSetting') THEN
        EXECUTE 'CREATE DATABASE "patternsUserSetting"';
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'patternsUsers') THEN
        EXECUTE 'CREATE DATABASE "patternsUsers"';
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'patterns_core_account') THEN
        EXECUTE 'CREATE DATABASE "patterns_core_account"';
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'patterns_credit') THEN
        EXECUTE 'CREATE DATABASE "patterns_credit"';
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'patterns_currency') THEN
        EXECUTE 'CREATE DATABASE "patterns_currency"';
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'patterns_monitoring') THEN
        EXECUTE 'CREATE DATABASE "patterns_monitoring"';
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'patterns_notification') THEN
        EXECUTE 'CREATE DATABASE "patterns_notification"';
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'patterns_gateway') THEN
        EXECUTE 'CREATE DATABASE "patterns_gateway"';
    END IF;
END
$$;
