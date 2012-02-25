#\ -p 9999 -E none -s thin
require 'rack/gridfs'

use Rack::GridFS, :prefix   => '', :lookup => :path, :mapper => lambda {|path| path[1..-1]},
                  :hostname => 'localhost', :port => 27017, :database => 'test'

run  proc { |env| [404, {'Content-Type' => 'text/plain'}, ['Not Found']] }
