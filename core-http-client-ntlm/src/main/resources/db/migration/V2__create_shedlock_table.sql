CREATE TABLE IF NOT EXISTS cep.shedlock
(
    name       VARCHAR(64)  NOT NULL,
    lock_until TIMESTAMP    NOT NULL,
    locked_at  TIMESTAMP    NOT NULL,
    locked_by  VARCHAR(255) NOT NULL,
    PRIMARY KEY (name)
);

COMMENT ON TABLE cep.shedlock IS 'Таблица для координации блокировок ShedLock в кластере';
COMMENT ON COLUMN cep.shedlock.name IS 'Имя блокировки (имя scheduled задачи)';
COMMENT ON COLUMN cep.shedlock.lock_until IS 'Время до которого блокировка действительна';
COMMENT ON COLUMN cep.shedlock.locked_at IS 'Время получения блокировки';
COMMENT ON COLUMN cep.shedlock.locked_by IS 'Идентификатор узла, который получил блокировку';
