import urlparse
import tld
from tld import get_tld
from tld.utils import update_tld_names

def add_scheme_if_missing(url):
    """
    >>> add_scheme_if_missing("example.com/foo")
    'http://example.com/foo'
    >>> add_scheme_if_missing("https://example.com/foo")
    'https://example.com/foo'
    >>> add_scheme_if_missing("//example.com/foo")
    'http://example.com/foo'
    """
    if url.startswith("//"):
        url = "http:" + url
    if "://" not in url:
        url = "http://" + url
    return url


def get_netloc(url):
    """
    >>> get_netloc("example.org/")
    'example.org'
    >>> get_netloc("http://example.org/foo")
    'example.org'
    >>> get_netloc("http://blog.example.org/foo")
    'blog.example.org'
    """
    '''return urlparse.urlparse(add_scheme_if_missing(url)).hostname'''
    return get_tld(add_scheme_if_missing(url))
