﻿SELECT u.id, r.shortname, u.username, u.email FROM
mdl_user u
JOIN mdl_role_assignments ra ON ra.userid = u.id
JOIN mdl_role r ON ra.roleid = r.id
JOIN mdl_context con ON ra.contextid = con.id
JOIN mdl_course c ON c.id = con.instanceid AND con.contextlevel = 50
WHERE
(r.shortname = 'teacher' OR r.shortname = 'editingteacher' OR r.shortname = 'student')
AND c.id = 