package org.grails.plugins.polls

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class Poll {

    List answers;
    static hasMany = [ answers : Answer ]

    String question
    Date startDate
    Date endDate
    Date dateCreated
    boolean isMultiple = false;
    boolean active = true

    static constraints = {
        question(blank: false, unique: true)
        startDate(nullable: false)
        endDate(nullable: true,
            validator: {val, obj ->
                return val?.after(obj.startDate)
            }
        )
    }

    static mapping = {
        def config = ConfigurationHolder.config
        if (config.grails.polls.poll.table) {
            table config.grails.polls.poll.table.toString()
        }
        else {
            table "polls"
        }

        question type: 'text'

        answers fetch:"join";
    }

    String toString() {
        return question + ', start date: ' + startDate;
    }

}
