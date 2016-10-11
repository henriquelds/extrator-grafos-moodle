SELECT id, course, type, name, intro, introformat, assessed, assesstimestart, 
       assesstimefinish, scale, maxbytes, maxattachments, forcesubscribe, 
       trackingtype, rsstype, rssarticles, timemodified, warnafter, 
       blockafter, blockperiod, completiondiscussions, completionreplies, 
       completionposts
  FROM mdl_forum WHERE course = '38';
