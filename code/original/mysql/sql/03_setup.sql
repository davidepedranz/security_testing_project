-- select the right database
USE schoolmate;

-- teachers
INSERT INTO `users` (`userid`, `username`, `password`, `type`) VALUES (2, 'teacher', md5('teacher'), 'Teacher');
INSERT INTO `teachers` (`teacherid`, `userid`, `fname`, lname) VALUES (1, 2, 'teacher', 'teacher');

-- substitute
INSERT INTO `users` (`userid`, `username`, `password`, `type`)
VALUES (5, 'substitute', md5('substitute'), 'Substitute');
INSERT INTO `teachers` (`teacherid`, `userid`, `fname`, lname) VALUES (2, 5, 'substitute', 'substitute');

-- terms
INSERT INTO `terms` (`termid`, `title`, `startdate`, `enddate`) VALUES (1, 'term', '2016-01-01', '2017-01-01');

-- semesters
INSERT INTO `semesters` (`semesterid`, `termid`, `title`, `startdate`, `midtermdate`, `enddate`)
VALUES (1, 1, 'semester', '2016-01-01', '2016-08-01', '2017-01-01');

-- courses
INSERT INTO `courses` (`courseid`, `semesterid`, `termid`, `coursename`, `teacherid`, `sectionnum`, `roomnum`, `periodnum`,
                       `q1points`, `q2points`, `totalpoints`, `aperc`, `bperc`, `cperc`, `dperc`, `fperc`, `substituteid`)
VALUES (1, 1, 1, 'course', 1, 'section', 'room', 'ppp',
           2.3, 4.5, 55.3, 1, 1, 1, 1, 1, 2);

-- students
INSERT INTO `users` (`userid`, `username`, `password`, `type`) VALUES (3, 'student', md5('student'), 'Student');
INSERT INTO `students` (`studentid`, `userid`, `fname`, `mi`, `lname`) VALUES (1, 3, 'name', 's', 'surname');

-- parents
INSERT INTO `users` (`userid`, `username`, `password`, `type`) VALUES (4, 'parent', md5('parent'), 'Parent');
INSERT INTO `parents` (`parentid`, `userid`, `fname`, `lname`) VALUES (1, 4, 'parent', 'parent');
INSERT INTO `parent_student_match` (`matchid`, `parentid`, `studentid`) VALUES (1, 1, 1);

-- registrations
INSERT INTO `registrations` (`regid`, `courseid`, `studentid`, `semesterid`, `termid`, `q1currpoints`, `q2currpoints`, `currentpoints`)
VALUES (1, 1, 1, 1, 1, 1.2, 2.3, 3.2);

-- assignments
INSERT INTO `assignments` (`assignmentid`, `courseid`, `semesterid`, `termid`, `title`, `totalpoints`, `assigneddate`, `duedate`, `assignmentinformation`)
VALUES (1, 1, 1, 1, 'assignment', 23, '2016-08-02', '2016-08-03', 'info');

