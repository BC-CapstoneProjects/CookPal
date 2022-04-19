import logger from 'jet-logger';


/**
 * Print an error object if it's truthy. Useful for testing.
 * 
 * @param err 
 */
export function pErr(err?: Error): void {
    if (!!err) {
        logger.err(err);
    }
};