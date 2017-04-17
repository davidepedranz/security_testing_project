-- users
INSERT INTO `users` (`username`, `password`, `type`) VALUES ('admin', 'admin', 'Admin');
INSERT INTO `users` (`userid`, `username`, `password`, `type`) VALUES (2, 'teacher', 'teacher', 'Teacher');

-- teachers
INSERT INTO `teachers` (`teacherid`, `userid`, `fname`, lname) VALUES (1, 2, 'teacher', 'teacher');

-- terms
INSERT INTO `terms` (`termid`, `title`, `startdate`, `enddate`) VALUES (1, 'term', '2016-01-01', '2017-01-01');

-- semesters
INSERT INTO `semesters` (`semesterid`, `termid`, `title`, `startdate`, `midtermdate`, `enddate`)
VALUES (1, 1, 'semester', '2016-01-01', '2016-08-01', '2017-01-01');

-- courses
INSERT INTO `courses` (`courseid`, `semesterid`, `termid`, `coursename`, `teacherid`, `sectionnum`, `roomnum`,
                       `periodnum`, `q1points`, `q2points`, `totalpoints`, `aperc`, `bperc`, `cperc`, `dperc`, `fperc`)
VALUES (1, 1, 1, 'course', 1, 'section', 'room',
           'ppp', 2.3, 4.5, 55.3, 1, 1, 1, 1, 1);
