SELECT p.id, p.parent, p.userid FROM
mdl_forum_posts p
JOIN mdl_forum_discussions d ON d.id = p.discussion
WHERE
d.course = 