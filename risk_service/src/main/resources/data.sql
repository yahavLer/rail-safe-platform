-- סולם חומרה
INSERT INTO risk_severity_scale (id, code, rating, fwsi_weight, hebrew_label, description)
VALUES
    (1, 'DISASTER', 4, 10.0, 'אסון', 'הרוגים מרובים ו/או נזק ישיר לרכוש מעל 65 מיליון ₪'),
    (2, 'CRITICAL', 3, 1.0, 'קריטי', 'מספר נמוך של הרוגים ו/או נזק 7–65 מיליון ₪'),
    (3, 'MODERATE', 2, 0.1, 'בינוני – גבולי', 'פצועים קשים / נזק 1–7 מיליון ₪'),
    (4, 'MINOR', 1, 0.01, 'זניח', 'פציעה קלה / נזק עד 1 מיליון ₪');

-- סולם הסתברות
INSERT INTO risk_likelihood_scale (id, code, rating, hebrew_label, description)
VALUES
    (1, 'RARE',       1, 'נדיר',          'אירוע אחת ל-10 שנים ומעלה'),
    (2, 'UNLIKELY',   2, 'לא סביר',       'שנה עד 10 שנים'),
    (3, 'OCCASIONAL', 3, 'תדירות נמוכה', 'חצי שנה עד שנה'),
    (4, 'FREQUENT',   4, 'מדי פעם',       'חודש עד חצי שנה');

-- מטריצת קבילות (דוגמה – אפשר לכוונן לפי הטבלה הרשמית)
-- risk_score = severity_rating * likelihood_rating
INSERT INTO risk_acceptance_matrix (severity_rating, likelihood_rating, risk_score, level)
VALUES
    (1,1,1,'ACCEPTABLE'),
    (1,2,2,'ACCEPTABLE'),
    (1,3,3,'TOLERABLE'),
    (1,4,4,'TOLERABLE'),
    (2,1,2,'ACCEPTABLE'),
    (2,2,4,'TOLERABLE'),
    (2,3,6,'TOLERABLE'),
    (2,4,8,'UNACCEPTABLE'),
    (3,1,3,'TOLERABLE'),
    (3,2,6,'TOLERABLE'),
    (3,3,9,'UNACCEPTABLE'),
    (3,4,12,'UNACCEPTABLE'),
    (4,1,4,'TOLERABLE'),
    (4,2,8,'UNACCEPTABLE'),
    (4,3,12,'UNACCEPTABLE'),
    (4,4,16,'UNACCEPTABLE');
