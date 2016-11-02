SELECT DISTINCT u.id FROM
mdl_user u
JOIN mdl_role_assignments ra ON ra.userid = u.id
JOIN mdl_role r ON ra.roleid = r.id
JOIN mdl_context con ON ra.contextid = con.id
JOIN mdl_course c ON c.id = con.instanceid AND con.contextlevel = 50
WHERE
(r.shortname = 'editingteacher')
AND (c.id = '35' OR c.id='36' OR c.id='37' OR c.id='38' OR c.id='39' OR c.id = '61' OR c.id='62' OR c.id='63' OR c.id='64' OR c.id='65') ORDER BY u.id;