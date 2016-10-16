/*
 * Date functions
 */

/**
 * Parse date to string
 * @param Date date
 * @returns string text
 */
function dateToStr(date) {
	var zeroFill = function(value) {
		value = value.toString();
		if (value.length < 2)
			value = '0' + value;
		return value;
	};
	var text;

	text  = date.getFullYear() + "-";
	text += zeroFill(date.getMonth() + 1) + "-";
	text += zeroFill(date.getDate()) + " ";

	text += zeroFill(date.getHours()) + ":";
	text += zeroFill(date.getMinutes()) + ":";
	text += zeroFill(date.getSeconds());
	return text;
}

/**
 * parse string to date
 * @param String str
 * @returns Date
 */
function parseStrToDate(str) {
	if (typeof str !== "string")
		return;
	var dateAndTime = str.split(' ');
	if (dateAndTime.length != 2)
		return;

	var date = dateAndTime[0];
	var dateParts = date.split('-');
	if (dateParts.length != 3)
		return;

	var year = dateParts[0];
	var month = dateParts[1];
	var day = dateParts[2];

	var time = dateAndTime[1];
	var timeParts = time.split(':');
	if (timeParts.length > 3 || timeParts.length < 2)
		return;

	var hour = timeParts[0];
	var minute = timeParts[1];

	var second = 0;
	if (timeParts.length == 3)
		second = timeParts[2];

	return new Date(year, month - 1, day, hour, minute, second);
}