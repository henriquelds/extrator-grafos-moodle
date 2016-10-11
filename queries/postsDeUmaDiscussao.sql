SELECT id, discussion, parent, userid, created, modified, mailed, subject, 
       message, messageformat, messagetrust, attachment, totalscore, 
       mailnow
  FROM mdl_forum_posts where discussion = '3042';
