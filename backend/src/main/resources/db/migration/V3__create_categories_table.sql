CREATE TABLE categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(10) NOT NULL CHECK (type IN ('INCOME','EXPENSE')),
    color VARCHAR(7),
    icon VARCHAR(50),
    is_system BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_user_category_type UNIQUE (user_id, name, type)
);
CREATE INDEX idx_categories_user_id ON categories(user_id);
CREATE INDEX idx_categories_type ON categories(type);

INSERT INTO categories (user_id, name, type, color, icon, is_system) VALUES
    (NULL, 'Salary',       'INCOME',  '#22C55E', 'briefcase',     TRUE),
    (NULL, 'Freelance',    'INCOME',  '#10B981', 'laptop',        TRUE),
    (NULL, 'Investment',   'INCOME',  '#3B82F6', 'trending-up',   TRUE),
    (NULL, 'Other Income', 'INCOME',  '#6EE7B7', 'plus-circle',   TRUE),
    (NULL, 'Food',         'EXPENSE', '#F97316', 'utensils',      TRUE),
    (NULL, 'Rent',         'EXPENSE', '#EF4444', 'home',          TRUE),
    (NULL, 'Transport',    'EXPENSE', '#8B5CF6', 'car',           TRUE),
    (NULL, 'Healthcare',   'EXPENSE', '#EC4899', 'heart-pulse',   TRUE),
    (NULL, 'Shopping',     'EXPENSE', '#F59E0B', 'shopping-bag',  TRUE),
    (NULL, 'Utilities',    'EXPENSE', '#6366F1', 'zap',           TRUE),
    (NULL, 'Entertainment','EXPENSE', '#14B8A6', 'tv',            TRUE),
    (NULL, 'Education',    'EXPENSE', '#0EA5E9', 'book',          TRUE);
