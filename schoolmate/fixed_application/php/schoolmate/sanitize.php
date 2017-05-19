<?php
/*
 * This PHP file contains some utilities used to fix the XSS vulnerabilities in this application
 * Author: Davide Pedranz
 */

/**
 * Sanitize a string to match an integer number.
 * This function will return the original string if it is safe,
 * otherwise return the default value 0.
 *
 * @param $raw String Raw user input string.
 * @return int Number extracted.
 */
function sanitize_digit($raw)
{
    return intval($raw);
}

?>