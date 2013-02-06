//
//  MessagesViewController.m
//  Parley
//
//  Created by Joshua Riddle on 2/5/13.
//  Copyright (c) 2013 The Riddle Brothers. All rights reserved.
//

#import "MessagesViewController.h"
#import "Message.h"
#import "MessageTableViewCell.h"


@implementation MessagesViewController

@synthesize messages;
@synthesize responseData;

- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    NSLog(@"viewdidload");
    self.responseData = [NSMutableData data];
 
    /*String[] hashtags = TWITTER_HASHTAGS.split(",");
     String query = "";
     for(int i = 0; i < hashtags.length; i++) {
     if (i != 0) query += "+OR+";
     query += hashtags[i];
     }
     if (lastId != null && lastId.length() != 0) query += "&since_id=" + lastId;
     
     // Search Twitter's public feed
     String url = "http://search.twitter.com/search.json?q=" + query
     + "&lang=en&result_type=recent&rpp=20";*/
    
    NSString *tags = @"Baltimore,Ravens";
    NSArray *tagArray = [tags componentsSeparatedByString:@","];
    
    NSMutableString *query = [[NSMutableString alloc] initWithString:@""];
    
    for(int i = 0; i < tagArray.count; i++) {
        if (i != 0) [query appendString:@"+OR+"];
        [query appendString:[tagArray objectAtIndex:i]];
    }
    
    //[query appendString:@"foobar"];
    
    NSString *url = [NSString stringWithFormat:@"%@%@%@",
                     @"http://search.twitter.com/search.json?q=",
                     query,
                     @"&lang=en&result_type=recent&rpp=20"];
    
    NSLog(@"Url: %@", url);

    NSURLRequest *request = [NSURLRequest requestWithURL:
                             [NSURL URLWithString:url]];
   
    (void)[[NSURLConnection alloc] initWithRequest:request delegate:self];
}



- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response {
    NSLog(@"didReceiveResponse");
    [self.responseData setLength:0];
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data {
    [self.responseData appendData:data];
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error {
    NSLog(@"didFailWithError");
    NSLog(@"Connection failed: %@", [error description]);
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection {
    NSLog(@"connectionDidFinishLoading");
    NSLog(@"Succeeded! Received %d bytes of data",[self.responseData length]);
    
    // convert to JSON
    NSError *myError = nil;
    NSDictionary *res = [NSJSONSerialization JSONObjectWithData:self.responseData options:NSJSONReadingMutableLeaves error:&myError];
   
    // extract specific value...
    NSArray *results = [res objectForKey:@"results"];
    
    self.messages = [[NSMutableArray alloc] initWithObjects:nil];
    for (NSDictionary *result in results) {
        NSString *text = [result objectForKey:@"text"];
        NSString *authorName = [result objectForKey:@"from_user"];
        NSString *rawDateCreated = [result objectForKey:@"created_at"];
        NSString *profileUrl = [result objectForKey:@"profile_image_url"];
        
        NSDateFormatter *format = [[NSDateFormatter alloc] init];
        [format setDateFormat:@"EEE, dd MMM yyyy HH:mm:ss Z"];
        NSDate *dateCreated = [format dateFromString:rawDateCreated];
        Message *m = [[Message alloc] initWithDetails:text authorName:authorName dateCreated:dateCreated profileUrl:profileUrl];
        [self.messages addObject:m];
    }
    [self.tableView reloadData];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    // Return the number of sections.
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.messages.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell";
    
    MessageTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    Message *m1 = [self.messages objectAtIndex:indexPath.row];
    
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateStyle:NSDateFormatterShortStyle];
    [dateFormatter setTimeStyle:NSDateFormatterNoStyle];
    NSString *formattedDateString = [dateFormatter stringFromDate:m1.dateCreated];
    
    [dateFormatter setDateStyle:NSDateFormatterNoStyle];
    [dateFormatter setTimeStyle:NSDateFormatterShortStyle];
    
    NSString *formattedTimeString = [dateFormatter stringFromDate:m1.dateCreated];
    NSString *detailsText = [NSString stringWithFormat:@"%@ at %@ on %@", m1.authorName, formattedDateString, formattedTimeString];
    [[cell messageLabel] setText:m1.message];
    [[cell detailsLabel] setText:detailsText];
    
    dispatch_async(dispatch_get_global_queue(0,0), ^{
        NSData * data = [[NSData alloc] initWithContentsOfURL: [NSURL URLWithString: m1.profileUrl]];
        if ( data == nil )
            return;
        dispatch_async(dispatch_get_main_queue(), ^{
            // WARNING: is the cell still using the same data by this point??
            cell.profileImage.image = [UIImage imageWithData: data];
        });
    });
    
    return cell;
}


- (NSString *)stringWithUrl:(NSURL *)url
{
    NSURLRequest *urlRequest = [NSURLRequest requestWithURL:url
                                                cachePolicy:NSURLRequestReturnCacheDataElseLoad
                                            timeoutInterval:30];
    // Fetch the JSON response
    NSData *urlData;
    NSURLResponse *response;
    NSError *error;
    
    // Make synchronous request
    urlData = [NSURLConnection sendSynchronousRequest:urlRequest
                                    returningResponse:&response
                                                error:&error];
    
    // Construct a String around the Data from the response
    return [[NSString alloc] initWithData:urlData encoding:NSUTF8StringEncoding];
}

/*
// Override to support conditional editing of the table view.
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Return NO if you do not want the specified item to be editable.
    return YES;
}
*/

/*
// Override to support editing the table view.
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        // Delete the row from the data source
        [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
    }   
    else if (editingStyle == UITableViewCellEditingStyleInsert) {
        // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
    }   
}
*/

/*
// Override to support rearranging the table view.
- (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath *)fromIndexPath toIndexPath:(NSIndexPath *)toIndexPath
{
}
*/

/*
// Override to support conditional rearranging of the table view.
- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Return NO if you do not want the item to be re-orderable.
    return YES;
}
*/

#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Navigation logic may go here. Create and push another view controller.
    /*
     <#DetailViewController#> *detailViewController = [[<#DetailViewController#> alloc] initWithNibName:@"<#Nib name#>" bundle:nil];
     // ...
     // Pass the selected object to the new view controller.
     [self.navigationController pushViewController:detailViewController animated:YES];
     */
}

@end
