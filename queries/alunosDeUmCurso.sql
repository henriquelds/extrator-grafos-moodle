SELECT distinct on (p.userid) p.userid, u.firstname, u.lastname FROM
mdl_forum_posts p
JOIN mdl_user u ON u.id = p.userid
JOIN mdl_role_assignments ra ON ra.userid = u.id
JOIN mdl_role r ON ra.roleid = r.id
JOIN mdl_context con ON ra.contextid = con.id
JOIN mdl_course c ON c.id = con.instanceid AND con.contextlevel = 50
JOIN mdl_forum_discussions d ON d.id = p.discussion AND d.course = c.id
WHERE
(r.shortname = 'student')
AND c.id = 


 