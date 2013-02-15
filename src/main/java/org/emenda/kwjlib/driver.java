package org.emenda.kwjlib;

import java.lang.String;

public class driver {

	/* This function contains a sample call to each function of the kwjlib.
	 * Note that some of these calls will fail if certain conditions do not exist,
	 * for example: a view can only be created for a project which exists, only
	 * a build which exists for a given project can be deleted etc.
	 */
	public static void main(String[] args) {
		String host = "";
		String port = "";
		String project_name = "";
		
		KWWebAPIService KWservice = new KWWebAPIService(host, port);
		
		if(KWservice.connect()) {
			KWJSONRecord[] records;
			//Get builds
			System.out.println("Retrieving builds");
			records = KWservice.builds(project_name);
			System.out.println(KWWebAPIService.getError());
			if(records != null) {
				for(KWJSONRecord build : records) {
					if(build == null) {
						break;
					}
					System.out.println(build.toString());
				}
			}
			else {
				System.out.println("\n******\nFailed.\n*******");
			}
			//Create a module
			System.out.println("Creating module");
			records = KWservice.create_module(project_name,
					"new module",
					"true",
					"",
					"",
					"",
					"",
					"**/test/*",
					"");
			System.out.println(KWWebAPIService.getError());
			if(records != null) {
				for(KWJSONRecord module : records) {
					if(module == null) {
						break;
					}
					System.out.println(module.toString());
				}
			}
			else {
				System.out.println("\n******\nFailed.\n*******");
			}
			//Create a view
			System.out.println("Creating view");
			records = KWservice.create_view(project_name,
					"new view",
					"severity:1-3",
					"",
					"true");
			System.out.println(KWWebAPIService.getError());
			if(records != null) {
				for(KWJSONRecord view : records) {
					if(view == null) {
						break;
					}
					System.out.println(view.toString());
				}
			}
			else {
				System.out.println("\n******\nFailed.\n*******");
			}
			//Delete a build
			System.out.println("Deleting build");
			records = KWservice.delete_build(project_name,
					"build_1");
			System.out.println(KWWebAPIService.getError());
			if(records != null) {
				for(KWJSONRecord view : records) {
					if(view == null) {
						break;
					}
					System.out.println(view.toString());
				}
			}
			else {
				System.out.println("\n******\nFailed.\n*******");
			}
			//Delete a module
			System.out.println("Deleting module");
			records = KWservice.delete_module(project_name,
					"new module");
			System.out.println(KWWebAPIService.getError());
			if(records != null) {
				for(KWJSONRecord module : records) {
					if(module == null) {
						break;
					}
					System.out.println(module.toString());
				}
			}
			else {
				System.out.println("\n******\nFailed.\n*******");
			}
			//Delete a project
			System.out.println("Deleting project");
			records = KWservice.delete_project(project_name);
			System.out.println(KWWebAPIService.getError());
			if(records != null) {
				for(KWJSONRecord module : records) {
					if(module == null) {
						break;
					}
					System.out.println(module.toString());
				}
			}
			else {
				System.out.println("\n******\nFailed.\n*******");
			}
			//Delete a view
			System.out.println("Deleting view");
			records = KWservice.delete_view(project_name,
					"new view");
			System.out.println(KWWebAPIService.getError());
			if(records != null) {
				for(KWJSONRecord view : records) {
					if(view == null) {
						break;
					}
					System.out.println(view.toString());
				}
			}
			else {
				System.out.println("\n******\nFailed.\n*******");
			}
			//Generate fchurns report
			System.out.println("Generating fchurns report");
			records = KWservice.fchurns(project_name,
					"hello",
					"andreas.larfors",
					"1",
					"");
			System.out.println(KWWebAPIService.getError());
			if(records != null) {
				for(KWJSONRecord report : records) {
					if(report == null) {
						break;
					}
					System.out.println(report.toString());
				}
			}
			else {
				System.out.println("\n******\nFailed.\n*******");
			}
			//Retrieve the list of modules
			System.out.println("Retrieving list of modules");
			records = KWservice.modules(project_name);
			System.out.println(KWWebAPIService.getError());
			if(records != null) {
				for(KWJSONRecord module : records) {
					if(module == null) {
						break;
					}
					System.out.println(module.toString());
				}
			}
			else {
				System.out.println("\n******\nFailed.\n*******");
			}
			//Retrieve the list of projects
			System.out.println("Retrieving list of projects");
			records = KWservice.projects();
			System.out.println(KWWebAPIService.getError());
			if(records != null) {
				for(KWJSONRecord module : records) {
					if(module == null) {
						break;
					}
					System.out.println(module.toString());
				}
			}
			else {
				System.out.println("\n******\nFailed.\n*******");
			}
			//Get issues
			System.out.println("Retrieving issues");
			records = KWservice.search(project_name, "");
			System.out.println("Error: " + KWWebAPIService.getError());
			System.out.println("Request sent: " + KWservice.getLastRequest());
			if(records != null) {
				for(KWJSONRecord issue : records) {
					if(issue == null) {
						break;
					}
					System.out.println(issue.toString());
				}
			}
			else {
				System.out.println("\n******\nFailed.\n*******");
			}
			//Generate build summary report
			System.out.println("Generating build summary report");
			records = KWservice.report(project_name,
					"build_2",
					"Severity:1-3",
					"",
					"Module",
					"",
					"Severity",
					"");
			System.out.println(KWWebAPIService.getError());
			if(records != null) {
				for(KWJSONRecord module : records) {
					if(module == null) {
						break;
					}
					System.out.println(module.toString());
				}
			}
			else {
				System.out.println("\n******\nFailed.\n*******");
			}
			//Update a build
			System.out.println("Updating a build");
			records = KWservice.update_build(project_name,
					"build_2",
					"",
					"");
			System.out.println(KWWebAPIService.getError());
			if(records != null) {
				for(KWJSONRecord module : records) {
					if(module == null) {
						break;
					}
					System.out.println(module.toString());
				}
			}
			else {
				System.out.println("\n******\nFailed.\n*******");
			}
			//Update a module
			System.out.println("Updating a module");
			records = KWservice.update_module(project_name,
					"module_01",
					"module_01",
					"true",
					"",
					"",
					"",
					"",
					"",
					"");
			System.out.println(KWWebAPIService.getError());
			if(records != null) {
				for(KWJSONRecord module : records) {
					if(module == null) {
						break;
					}
					System.out.println(module.toString());
				}
			}
			else {
				System.out.println("\n******\nFailed.\n*******");
			}
			//Update a project
			System.out.println("Updating a project");
			records = KWservice.update_project(project_name,
					project_name,
					"a new description",
					"");
			System.out.println(KWWebAPIService.getError());
			if(records != null) {
				for(KWJSONRecord module : records) {
					if(module == null) {
						break;
					}
					System.out.println(module.toString());
				}
			}
			else {
				System.out.println("\n******\nFailed.\n*******");
			}
			//Update a view
			System.out.println("Updating a view");
			records = KWservice.update_view(project_name,
					"hello",
					"",
					"",
					"",
					"");
			System.out.println(KWWebAPIService.getError());
			if(records != null) {
				for(KWJSONRecord module : records) {
					if(module == null) {
						break;
					}
					System.out.println(module.toString());
				}
			}
			else {
				System.out.println("\n******\nFailed.\n*******");
			}
			//Retrieve list of views
			System.out.println("Retrieving list of views");
			records = KWservice.views(project_name);
			System.out.println(KWWebAPIService.getError());
			if(records != null) {
				for(KWJSONRecord module : records) {
					if(module == null) {
						break;
					}
					System.out.println(module.toString());
				}
			}
			else {
				System.out.println("\n******\nFailed.\n*******");
			}
		}
		
	}

}
