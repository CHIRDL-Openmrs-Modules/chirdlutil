	
function resetLogForm(initialEndMonth, initialEndDay, initialEndYear) {
    document.getElementById('locationId').selectedIndex = 0;
    document.getElementById('formId').selectedIndex = 0;
    document.getElementById('event').selectedIndex = 0;
    document.getElementById('user').selectedIndex = 0;
    document.getElementById('startDateMonth').selectedIndex = 0;
    document.getElementById('startDateDay').selectedIndex = 0;
    var startDateYear = document.getElementById('startDateYear');
    startDateYear.selectedIndex = startDateYear.options.length - 1;
    
    var endDateMonth = document.getElementById('endDateMonth');
    for (i = 0; i < endDateMonth.options.length; i++) {
        if (endDateMonth.options[i].value == initialEndMonth) {
        	endDateMonth.selectedIndex = i;
            break;
        }
    }
    
    var endDateDay = document.getElementById('endDateDay');
    for (i = 0; i < endDateDay.options.length; i++) {
        if (endDateDay.options[i].value == initialEndDay) {
        	endDateDay.selectedIndex = i;
            break;
        }
    }
    
    var endDateYear = document.getElementById('endDateYear');
    for (i = 0; i < endDateYear.options.length; i++) {
    	if (endDateYear.options[i].value == initialEndYear) {
    		endDateYear.selectedIndex = i;
    		break;
    	}
    }
    
    document.getElementById('studyId').value = '';
    document.getElementById('description').value = '';
}

